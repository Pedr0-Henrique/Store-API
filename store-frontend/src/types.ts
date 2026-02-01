export type Category = {
  id: number
  name: string
  description?: string
  createdAt: string
}

export type Product = {
  id: number
  name: string
  description?: string
  price: number
  categoryId: number
  categoryName: string
  createdAt: string
}

export type Customer = {
  id: number
  name: string
  email: string
  phone?: string
  createdAt: string
}

export type OrderItem = {
  productId: number
  productName: string
  quantity: number
  unitPrice?: number
  subtotal?: number
}

export type Order = {
  id: number
  customerId: number
  customerName: string
  items: OrderItem[]
  total: number
  status: 'CREATED' | 'PENDING' | 'PAID' | 'DELIVERED' | 'CANCELED'
  createdAt: string
}

export type CustomerOrderCount = {
  total: number
  open: number
}
