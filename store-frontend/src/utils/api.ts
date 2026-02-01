import axios from 'axios'

const baseURL = (import.meta as any)?.env?.VITE_API_URL ?? '/api/v1'

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
})

export function getApiErrorMessage(e: any): string {
  const details: string[] | undefined = e?.response?.data?.details
  if (Array.isArray(details) && details.length > 0) return details.join('\n')
  return e?.response?.data?.message || e?.response?.data?.error || e?.message || 'Erro inesperado'
}

export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
