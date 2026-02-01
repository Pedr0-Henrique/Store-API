/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        background: '#0b0f1a',
        foreground: '#e6edf6',
        primary: {
          DEFAULT: '#6e56cf',
          foreground: '#ffffff',
        },
        muted: '#111827',
        card: '#0f1629',
        border: '#1f2937'
      },
      boxShadow: {
        soft: '0 10px 30px rgba(0,0,0,0.3)'
      }
    },
  },
  plugins: [],
}
