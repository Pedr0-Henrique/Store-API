import { ButtonHTMLAttributes } from 'react'
import { twMerge } from 'tailwind-merge'

export function Button({ className, variant = 'solid', ...props }: ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'solid'|'outline'|'ghost' }) {
  const base = 'btn'
  const variants = {
    solid: 'btn',
    outline: 'btn btn-outline',
    ghost: 'inline-flex items-center gap-2 rounded-md px-4 py-2 text-sm text-foreground/80 hover:bg-card'
  }
  return <button className={twMerge(base, variants[variant], className)} {...props} />
}
