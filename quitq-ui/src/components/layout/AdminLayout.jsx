import { useEffect } from "react"
import { Outlet, useNavigate, useParams } from "react-router"
import Navbar from "./Navbar"

function AdminLayout() {
    const params = useParams()
    const navigate = useNavigate()

    const memToken = localStorage.getItem('token')
    const valid = Boolean(memToken) && params.token === memToken
        && localStorage.getItem('role') === 'ROLE_ADMIN'

    useEffect(() => {
        const memToken = localStorage.getItem('token')
        if (!memToken || params.token !== memToken || localStorage.getItem('role') !== 'ROLE_ADMIN') {
            navigate("/")
        }
    }, [params.token, navigate])

    // Invalid session -> do not mount children (no API calls fire)
    if (!valid) return null

    return (
        <div className="d-flex flex-column" style={{ minHeight: '100vh' }}>
            <Navbar />
            <div className="flex-grow-1">
                <Outlet />
            </div>
            <footer className="fk-footer">
                <div className="d-flex align-items-center justify-content-center gap-3 flex-wrap">
                    <img src="/logo-transparent.png" alt="QuitQ" style={{ height: 34 }} />
                    <span className="fk-footer-tag">SHOP QUICK, SHOP EASY</span>
                    <button className="fk-footer-top" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>
                        <i className="bi bi-arrow-up me-1"></i>Back to top
                    </button>
                </div>
                <div className="mt-2" style={{ opacity: 0.7, fontSize: 12 }}>&copy; 2026 QuitQ Technologies</div>
            </footer>
        </div>
    )
}

export default AdminLayout
