import { Routes, Route, NavLink, Navigate } from 'react-router-dom'
import { CategoriesPage } from './pages/Categories'
import { ProductsPage } from './pages/Products'
import { CustomersPage } from './pages/Customers'
import { OrdersPage } from './pages/Orders'
import { Navbar } from './components/ui/navbar'

export default function App() {
  return (
    <div className="min-h-screen bg-background text-foreground">
      <Navbar>
        <NavLink to="/categories" className={({isActive}) => isActive ? 'text-primary font-semibold' : 'text-foreground/80 hover:text-foreground'}>Categorias</NavLink>
        <NavLink to="/products" className={({isActive}) => isActive ? 'text-primary font-semibold' : 'text-foreground/80 hover:text-foreground'}>Produtos</NavLink>
        <NavLink to="/customers" className={({isActive}) => isActive ? 'text-primary font-semibold' : 'text-foreground/80 hover:text-foreground'}>Clientes</NavLink>
        <NavLink to="/orders" className={({isActive}) => isActive ? 'text-primary font-semibold' : 'text-foreground/80 hover:text-foreground'}>Pedidos</NavLink>
      </Navbar>
      <main className="container mx-auto px-4 py-8 max-w-6xl">
        <Routes>
          <Route path="/" element={<Navigate to="/categories" replace />} />
          <Route path="/categories" element={<CategoriesPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/customers" element={<CustomersPage />} />
          <Route path="/orders" element={<OrdersPage />} />
        </Routes>
      </main>
    </div>
  )
}
