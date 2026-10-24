import { Button } from '@/compartilhado/componentes/ui/button'

function App() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-4">
      <h1 className="text-2xl font-semibold">Real Estate Platform</h1>
      <Button onClick={() => alert('Tailwind e shadcn/ui funcionando')}>Testar</Button>
    </main>
  )
}

export default App
