import { ReactNode } from 'react'
import { Store, Github } from 'lucide-react'
import { Link } from 'react-router-dom'

export function Navbar({ children }: { children?: ReactNode }) {
  return (
    <header className="sticky top-0 z-40 w-full border-b border-border/70 backdrop-blur supports-[backdrop-filter]:bg-background/70">
      <div className="container mx-auto flex h-16 max-w-6xl items-center justify-between">
        <Link to="/" className="inline-flex items-center gap-2">
          <div className="grid h-9 w-9 place-items-center rounded-md bg-primary text-white shadow-soft">
            <Store size={18} />
          </div>
          <div className="leading-tight">
            <div className="text-sm text-foreground/70">Admin</div>
            <div className="-mt-0.5 text-lg font-semibold">Store</div>
          </div>
        </Link>
        <nav className="flex items-center gap-6 text-sm">
          {children}
        </nav>
        <a href="https://github.com" target="_blank" rel="noreferrer" className="text-foreground/60 hover:text-foreground">
          <Github size={18} />
        </a>
      </div>
    </header>
  )
}
