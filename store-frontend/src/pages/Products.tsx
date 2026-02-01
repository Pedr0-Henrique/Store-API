import { useEffect, useMemo, useState } from 'react'
import { api, getApiErrorMessage, Page } from '../utils/api'
import { Category, Product } from '../types'
import { Card, CardBody, CardHeader } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Modal } from '../components/ui/modal'
import { toast } from 'sonner'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import { Pagination } from '../components/ui/pagination'

export function ProductsPage() {
  const [items, setItems] = useState<Product[]>([])
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)

  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Product | null>(null)
  const [form, setForm] = useState({ name: '', description: '', price: 0, categoryId: 0 })
  const [categories, setCategories] = useState<Category[]>([])

  const title = useMemo(() => editing ? 'Editar produto' : 'Novo produto', [editing])

  async function load() {
    setLoading(true)
    try {
      const res = await api.get<Page<Product>>('/products', { params: { page, size, sort: 'createdAt,desc' } })
      setItems(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch (e: any) {
      toast.error('Falha ao carregar produtos', { description: getApiErrorMessage(e) })
    } finally {
      setLoading(false)
    }
  }

  async function loadCategories() {
    try {
      const res = await api.get<Page<Category>>('/categories', { params: { page: 0, size: 100, sort: 'name,asc' } })
      setCategories(res.data.content)
    } catch (e: any) {
      toast.error('Falha ao carregar categorias', { description: getApiErrorMessage(e) })
    }
  }

  useEffect(() => { load() }, [page, size])

  function openCreate() {
    setEditing(null)
    setForm({ name: '', description: '', price: 0, categoryId: 0 })
    setOpen(true)
    loadCategories()
  }

  function openEdit(item: Product) {
    setEditing(item)
    setForm({ name: item.name, description: item.description ?? '', price: item.price, categoryId: item.categoryId })
    setOpen(true)
    loadCategories()
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    try {
      if (form.categoryId === 0) {
        toast.error('Selecione uma categoria')
        return
      }
      const payload = { ...form, price: Number(form.price) }
      if (editing) {
        await api.patch(`/products/${editing.id}`, payload)
        toast.success('Produto atualizado')
      } else {
        await api.post('/products', payload)
        toast.success('Produto criado')
      }
      setOpen(false)
      load()
    } catch (e: any) {
      toast.error('Erro ao salvar', { description: getApiErrorMessage(e) })
    }
  }

  async function onDelete(id: number) {
    if (!confirm('Tem certeza que deseja excluir?')) return
    try {
      await api.delete(`/products/${id}`)
      toast.success('Produto excluído')
      load()
    } catch (e: any) {
      toast.error('Erro ao excluir', { description: getApiErrorMessage(e) })
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Produtos</h1>
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
                <th>Nome</th>
                <th>Preço</th>
                <th>Categoria</th>
                <th className="w-40 text-right">Ações</th>
              </tr>
              </thead>
              <tbody>
              {items.map(i => (
                <tr key={i.id} className="hover:bg-white/5">
                  <td>#{i.id}</td>
                  <td>{i.name}</td>
                  <td>R$ {Number(i.price).toFixed(2)}</td>
                  <td><span className="badge">{i.categoryName}</span></td>
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
                  <td colSpan={5} className="py-8 text-center text-foreground/60">Nenhum registro.</td>
                </tr>
              )}
              {loading && (
                <tr>
                  <td colSpan={5} className="py-8 text-center text-foreground/60">Carregando...</td>
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
            <label className="label">Nome</label>
            <input className="input mt-1" value={form.name} required maxLength={160}
                   onChange={e => setForm({ ...form, name: e.target.value })} />
          </div>
          <div>
            <label className="label">Descrição</label>
            <input className="input mt-1" value={form.description} maxLength={255}
                   onChange={e => setForm({ ...form, description: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Preço</label>
              <input type="number" min="0" step="0.01" className="input mt-1" value={form.price}
                     onChange={e => setForm({ ...form, price: Number(e.target.value) })} required />
            </div>
            <div>
              <label className="label">Categoria</label>
              <select className="input mt-1" value={form.categoryId}
                      onChange={e => setForm({ ...form, categoryId: Number(e.target.value) })} required>
                <option value={0}>Selecione...</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancelar</Button>
            <Button type="submit">Salvar</Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
