import { useEffect, useMemo, useState } from 'react'
import { api, getApiErrorMessage, Page } from '../utils/api'
import { Category } from '../types'
import { Card, CardBody, CardHeader } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Modal } from '../components/ui/modal'
import { toast } from 'sonner'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import { Pagination } from '../components/ui/pagination'

export function CategoriesPage() {
  const [items, setItems] = useState<Category[]>([])
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)

  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Category | null>(null)
  const [form, setForm] = useState({ name: '', description: '' })

  const title = useMemo(() => editing ? 'Editar categoria' : 'Nova categoria', [editing])

  async function load() {
    setLoading(true)
    try {
      const res = await api.get<Page<Category>>('/categories', { params: { page, size, sort: 'createdAt,desc' } })
      setItems(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch (e: any) {
      toast.error('Falha ao carregar categorias', { description: getApiErrorMessage(e) })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [page, size])

  function openCreate() {
    setEditing(null)
    setForm({ name: '', description: '' })
    setOpen(true)
  }

  function openEdit(item: Category) {
    setEditing(item)
    setForm({ name: item.name, description: item.description ?? '' })
    setOpen(true)
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    try {
      if (editing) {
        await api.patch(`/categories/${editing.id}`, form)
        toast.success('Categoria atualizada')
      } else {
        await api.post('/categories', form)
        toast.success('Categoria criada')
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
      await api.delete(`/categories/${id}`)
      toast.success('Categoria excluída')
      load()
    } catch (e: any) {
      toast.error('Erro ao excluir', { description: getApiErrorMessage(e) })
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Categorias</h1>
        <div className="flex items-center gap-2">
          <select className="input h-10 w-28" value={size} onChange={(e) => setSize(parseInt(e.target.value))}>
            {[5,10,20,50].map(s => <option key={s} value={s}>{s}/página</option>)}
          </select>
          <Button onClick={openCreate}><Plus size={16}/> Nova</Button>
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
                <th>Descrição</th>
                <th className="w-40 text-right">Ações</th>
              </tr>
              </thead>
              <tbody>
              {items.map(i => (
                <tr key={i.id} className="hover:bg-white/5">
                  <td>#{i.id}</td>
                  <td>{i.name}</td>
                  <td className="text-foreground/80">{i.description}</td>
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
                  <td colSpan={4} className="py-8 text-center text-foreground/60">Nenhum registro.</td>
                </tr>
              )}
              {loading && (
                <tr>
                  <td colSpan={4} className="py-8 text-center text-foreground/60">Carregando...</td>
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
            <input className="input mt-1" value={form.name} required maxLength={120}
                   onChange={e => setForm({ ...form, name: e.target.value })} />
          </div>
          <div>
            <label className="label">Descrição</label>
            <input className="input mt-1" value={form.description} maxLength={255}
                   onChange={e => setForm({ ...form, description: e.target.value })} />
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
