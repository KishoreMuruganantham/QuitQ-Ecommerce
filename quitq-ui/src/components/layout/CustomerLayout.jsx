import { useEffect } from "react"
import { Outlet, useNavigate, useParams } from "react-router"
import Navbar from "./Navbar"

function CustomerLayout() {
    const params = useParams()
    const navigate = useNavigate()

    const memToken = localStorage.getItem('token')
    const valid = Boolean(memToken) && params.token === memToken
        && localStorage.getItem('role') === 'ROLE_CUSTOMER'

    useEffect(() => {
        const memToken = localStorage.getItem('token')
        if (!memToken || params.token !== memToken || localStorage.getItem('role') !== 'ROLE_CUSTOMER') {
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
            <footer className="fk-footer">&copy; 2026 QuitQ &middot; Shop Quick, Shop Easy</footer>
        </div>
    )
}

export default CustomerLayout
