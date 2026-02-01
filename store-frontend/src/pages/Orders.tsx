import { useEffect, useMemo, useState } from 'react'
import { api, getApiErrorMessage, Page } from '../utils/api'
import { Customer, CustomerOrderCount, Order, Product } from '../types'
import { Card, CardBody, CardHeader } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Modal } from '../components/ui/modal'
import { toast } from 'sonner'
import { Plus, Trash2, Pencil } from 'lucide-react'
import { Pagination } from '../components/ui/pagination'

type NewOrderItem = { productId: number; quantity: number }

export function OrdersPage() {
  const [items, setItems] = useState<Order[]>([])
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)

  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Order | null>(null)
  const [customers, setCustomers] = useState<Customer[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [formCustomerId, setFormCustomerId] = useState<number>(0)
  const [formItems, setFormItems] = useState<NewOrderItem[]>([])
  const [formStatus, setFormStatus] = useState<'CREATED' | 'PENDING' | 'PAID' | 'DELIVERED' | 'CANCELED'>('CREATED')
  const [initialStatus, setInitialStatus] = useState<'CREATED' | 'PENDING' | 'PAID' | 'DELIVERED' | 'CANCELED'>('CREATED')
  const [orderCount, setOrderCount] = useState<CustomerOrderCount | null>(null)

  const title = useMemo(() => editing ? 'Editar pedido' : 'Novo pedido', [editing])

  const total = useMemo(() => {
    return formItems.reduce((acc, it) => {
      const p = products.find(pr => pr.id === it.productId)
      if (!p) return acc
      return acc + Number(p.price) * Number(it.quantity)
    }, 0)
  }, [formItems, products])

  function normalizeItemsForCompare(items: { productId: number; quantity: number }[]) {
    return items.map(it => ({ productId: Number(it.productId), quantity: Number(it.quantity) }))
  }

  async function load() {
    setLoading(true)
    try {
      const res = await api.get<Page<Order>>('/orders', { params: { page, size, sort: 'createdAt,desc' } })
      setItems(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch (e: any) {
      toast.error('Falha ao carregar pedidos', { description: getApiErrorMessage(e) })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [page, size])

  function openCreate() {
    setEditing(null)
    setFormCustomerId(0)
    setFormItems([])
    setFormStatus('CREATED')
    setInitialStatus('CREATED')
    setOpen(true)
    loadAuxData()
  }

  async function openEdit(order: Order) {
    setEditing(order)
    setFormCustomerId(order.customerId)
    const mapped: NewOrderItem[] = (order.items || []).map(it => ({ productId: it.productId, quantity: it.quantity }))
    setFormItems(mapped)
    setFormStatus(order.status)
    setInitialStatus(order.status)
    setOpen(true)
    loadAuxData()
  }

  async function loadAuxData() {
    try {
      const [cRes, pRes] = await Promise.all([
        api.get<Page<Customer>>('/customers', { params: { page: 0, size: 100, sort: 'name,asc' } }),
        api.get<Page<Product>>('/products', { params: { page: 0, size: 200, sort: 'name,asc' } }),
      ])
      setCustomers(cRes.data.content)
      setProducts(pRes.data.content)
    } catch (e: any) {
      toast.error('Falha ao carregar clientes/produtos', { description: getApiErrorMessage(e) })
    }
  }

  async function loadOrderCount(customerId: number) {
    if (!customerId) {
      setOrderCount(null)
      return
    }
    try {
      const res = await api.get<CustomerOrderCount>(`/customers/${customerId}/orders/count`)
      setOrderCount(res.data)
    } catch (e: any) {
      setOrderCount(null)
    }
  }

  function addItem() {
    setFormItems(prev => [...prev, { productId: 0, quantity: 1 }])
  }

  function updateItem(idx: number, patch: Partial<NewOrderItem>) {
    setFormItems(prev => prev.map((it, i) => i === idx ? { ...it, ...patch } : it))
  }

  function removeItem(idx: number) {
    setFormItems(prev => prev.filter((_, i) => i !== idx))
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    try {
      if (formCustomerId === 0) {
        toast.error('Selecione um cliente')
        return
      }

      const hasValidItems = formItems.length > 0 && !formItems.some(it => it.productId === 0 || it.quantity <= 0)

      if (editing) {
        const statusChanged = formStatus !== initialStatus
        const customerChanged = formCustomerId !== editing.customerId

        const originalItems = normalizeItemsForCompare(
          (editing.items || []).map(it => ({ productId: it.productId, quantity: it.quantity }))
        )
        const currentItems = normalizeItemsForCompare(formItems)
        const itemsChanged = JSON.stringify(originalItems) !== JSON.stringify(currentItems)

        // 1) Status sempre via endpoint dedicado (mantém validações de transição)
        if (statusChanged) {
          await api.patch(`/orders/${editing.id}/status`, { status: formStatus })
        }

        // 2) Alterações estruturais (cliente/itens) via PUT (sem reenviar status)
        if (customerChanged || itemsChanged) {
          if (!hasValidItems) {
            toast.error('Adicione pelo menos um item válido')
            return
          }

          await api.put(`/orders/${editing.id}`, {
            customerId: formCustomerId,
            items: formItems.map(i => ({ productId: i.productId, quantity: Number(i.quantity) })),
          })
        }

        if (!statusChanged && !customerChanged && !itemsChanged) {
          toast.message('Nenhuma alteração para salvar')
          return
        }

        toast.success('Pedido atualizado')
      } else {
        if (!hasValidItems) {
          toast.error('Adicione pelo menos um item válido')
          return
        }
        // opcional: enviar status inicial; backend pode padronizar para PENDING
        // payload.status = formStatus
        await api.post('/orders', {
          customerId: formCustomerId,
          items: formItems.map(i => ({ productId: i.productId, quantity: Number(i.quantity) })),
        })
        toast.success('Pedido criado')
      }
      setOpen(false)
      await loadOrderCount(formCustomerId)
      load()
    } catch (e: any) {
      toast.error('Erro ao salvar', { description: getApiErrorMessage(e) })
    }
  }

  async function onDelete(id: number) {
    if (!confirm('Tem certeza que deseja excluir?')) return
    try {
      await api.delete(`/orders/${id}`)
      toast.success('Pedido excluído')
      load()
    } catch (e: any) {
      toast.error('Erro ao excluir', { description: getApiErrorMessage(e) })
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Pedidos</h1>
        <div className="flex items-center gap-2">
          <select className="input h-10 w-28" value={size} onChange={(e) => setSize(parseInt(e.target.value))}>
            {[5,10,20,50].map(s => <option key={s} value={s}>{s}/página</option>)}
          </select>
          <Button onClick={openCreate}><Plus size={16}/> Novo</Button>
        </div>
      </div>

      <Card>
        <CardHeader>Lista</CardHeader>
        <CardBody>
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Total</th>
                <th>Status</th>
                <th>Data</th>
                <th className="w-40 text-right">Ações</th>
              </tr>
              </thead>
              <tbody>
              {items.map(i => (
                <tr key={i.id} className="hover:bg-white/5">
                  <td>#{i.id}</td>
                  <td>{i.customerName}</td>
                  <td>R$ {Number(i.total).toFixed(2)}</td>
                  <td><span className="badge">{i.status}</span></td>
                  <td className="text-foreground/80">{new Date(i.createdAt).toLocaleString()}</td>
                  <td>
                    <div className="flex justify-end gap-2">
                      <Button variant="outline" onClick={() => openEdit(i)}><Pencil size={14}/> Editar</Button>
                      <Button onClick={() => onDelete(i.id)}><Trash2 size={14}/> Excluir</Button>
                    </div>
                  </td>
                </tr>
              ))}
              {!loading && items.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-foreground/60">Nenhum registro.</td>
                </tr>
              )}
              {loading && (
                <tr>
                  <td colSpan={6} className="py-8 text-center text-foreground/60">Carregando...</td>
                </tr>
              )}
              </tbody>
            </table>
          </div>
          <Pagination page={page} size={size} totalPages={totalPages} onChange={setPage} />
        </CardBody>
      </Card>

      <Modal open={open} onOpenChange={setOpen} title={title}>
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="label">Cliente</label>
            <select
              className="input mt-1"
              value={formCustomerId}
              onChange={async (e) => {
                const id = Number(e.target.value)
                setFormCustomerId(id)
                await loadOrderCount(id)
              }}
              required
            >
              <option value={0}>Selecione...</option>
              {customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
            {orderCount && (
              <div className="mt-2 text-xs text-foreground/70">
                Pedidos em aberto: <strong>{orderCount.open}</strong> · Total: <strong>{orderCount.total}</strong>
              </div>
            )}
          </div>
          {editing && (
            <div>
              <label className="label">Status</label>
              <select className="input mt-1" value={formStatus} onChange={e => setFormStatus(e.target.value as any)}>
                <option value="CREATED">CREATED</option>
                <option value="PENDING">PENDING</option>
                <option value="PAID">PAID</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="CANCELED">CANCELED</option>
              </select>
            </div>
          )}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <label className="label">Itens</label>
              <Button type="button" variant="outline" onClick={addItem}><Plus size={14}/> Adicionar item</Button>
            </div>
            {formItems.length === 0 && (
              <div className="text-sm text-foreground/60">Nenhum item adicionado.</div>
            )}
            {formItems.map((it, idx) => (
              <div key={idx} className="grid grid-cols-1 md:grid-cols-12 gap-3 items-end">
                <div className="md:col-span-7">
                  <label className="label">Produto</label>
                  <select className="input mt-1" value={it.productId} onChange={e => updateItem(idx, { productId: Number(e.target.value) })} required>
                    <option value={0}>Selecione...</option>
                    {products.map(p => <option key={p.id} value={p.id}>{p.name} — R$ {Number(p.price).toFixed(2)}</option>)}
                  </select>
                </div>
                <div className="md:col-span-2">
                  <label className="label">Qtd</label>
                  <input type="number" min="1" className="input mt-1" value={it.quantity}
                         onChange={e => updateItem(idx, { quantity: Number(e.target.value) })} required />
                </div>
                <div className="md:col-span-3 flex justify-end">
                  <Button className="w-full justify-center whitespace-nowrap" type="button" variant="outline" onClick={() => removeItem(idx)}>
                    <Trash2 size={14} />
                    <span className="hidden sm:inline">Remover</span>
                  </Button>
                </div>
              </div>
            ))}
          </div>
          <div className="flex justify-between items-center pt-2">
            <div className="text-sm text-foreground/80">Total: <strong>R$ {total.toFixed(2)}</strong></div>
            <div className="flex gap-2">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button type="submit">Salvar</Button>
            </div>
          </div>
        </form>
      </Modal>
    </div>
  )
}
