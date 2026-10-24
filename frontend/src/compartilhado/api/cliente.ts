import createClient from 'openapi-fetch'
import type { paths } from './esquema'

export const api = createClient<paths>({ baseUrl: '/api' })
