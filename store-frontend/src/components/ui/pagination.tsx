type Props = {
  page: number
  size: number
  totalPages: number
  onChange: (page: number) => void
}

export function Pagination({ page, size, totalPages, onChange }: Props) {
  const canPrev = page > 0
  const canNext = page + 1 < totalPages
  return (
    <div className="pagination">
      <div className="text-sm text-foreground/70">
        Página {page + 1} de {Math.max(totalPages, 1)}
      </div>
      <div className="flex items-center gap-2">
        <button className="btn btn-outline" disabled={!canPrev} onClick={() => onChange(0)}>« Primeira</button>
        <button className="btn btn-outline" disabled={!canPrev} onClick={() => onChange(page - 1)}>‹ Anterior</button>
        <button className="btn" disabled={!canNext} onClick={() => onChange(page + 1)}>Próxima ›</button>
        <button className="btn" disabled={!canNext} onClick={() => onChange(totalPages - 1)}>Última »</button>
      </div>
    </div>
  )
}
