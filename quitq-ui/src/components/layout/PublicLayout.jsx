import { Outlet } from "react-router"
import Navbar from "./Navbar"
import Footer from "./Footer"

function PublicLayout() {
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

export default PublicLayout
