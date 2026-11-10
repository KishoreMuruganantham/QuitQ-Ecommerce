import { useEffect } from "react"
import { Outlet, useNavigate, useParams } from "react-router"
import Navbar from "./Navbar"
import Footer from "./Footer"

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
            <Footer />
        </div>
    )
}

export default AdminLayout
