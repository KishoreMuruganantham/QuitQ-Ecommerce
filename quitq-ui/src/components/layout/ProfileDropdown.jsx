import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function ProfileDropdown() {
    const navigate = useNavigate()
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('role')
    const username = localStorage.getItem('username')

    const [open, setOpen] = useState(false)
    const wrapperRef = useRef(null)

    // Close when clicking outside or pressing Escape
    useEffect(() => {
        const onClickAway = (e) => {
            if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
                setOpen(false)
            }
        }
        const onEscape = (e) => {
            if (e.key === 'Escape') setOpen(false)
        }
        document.addEventListener('mousedown', onClickAway)
        document.addEventListener('keydown', onEscape)
        return () => {
            document.removeEventListener('mousedown', onClickAway)
            document.removeEventListener('keydown', onEscape)
        }
    }, [])

    const roleLabel = role === 'ROLE_SELLER' ? 'Seller'
        : role === 'ROLE_ADMIN' ? 'Administrator'
            : 'Customer'

    const go = (path) => {
        setOpen(false)
        navigate(path)
    }

    const logout = async () => {
        setOpen(false)
        try {
            await axios.post(API_SERVER_URL_DEV + '/api/auth/logout', null, {
                headers: { 'Authorization': 'Bearer ' + token }
            })
        } catch {
            // token may already be invalid, still clear the session
        }
        localStorage.clear()
        navigate("/")
    }

    return (
        <div className="fk-dropdown" ref={wrapperRef}>
            {/* <!-- Trigger: avatar + username + caret --> */}
            <button type="button" className="fk-dropdown-trigger"
                onClick={() => setOpen(!open)}
                aria-expanded={open} aria-haspopup="true">
                <i className="bi bi-person-circle"></i>
                <span className="d-none d-sm-inline">{username}</span>
                <i className={`bi bi-chevron-down fk-dropdown-caret ${open ? 'fk-dropdown-caret-open' : ''}`}></i>
            </button>

            {/* <!-- Menu --> */}
            {open && (
                <div className="fk-dropdown-menu" role="menu">

                    {/* <!-- Profile section --> */}
                    <div className="fk-dropdown-profile">
                        <span className="fk-avatar">{username ? username.charAt(0).toUpperCase() : 'U'}</span>
                        <div className="d-flex flex-column">
                            <span className="fw-semibold">{username}</span>
                            <span className="fk-muted" style={{ fontSize: 12 }}>{roleLabel}</span>
                        </div>
                    </div>
                    <div className="fk-dropdown-separator"></div>

                    {/* <!-- Role based menu items --> */}
                    {role === 'ROLE_CUSTOMER' && (
                        <button type="button" className="fk-dropdown-item" role="menuitem"
                            onClick={() => go(`/customer/${token}/orders`)}>
                            <i className="bi bi-box-seam"></i>My Orders
                        </button>
                    )}
                    {role === 'ROLE_SELLER' && (
                        <>
                            <button type="button" className="fk-dropdown-item" role="menuitem"
                                onClick={() => go(`/seller/${token}`)}>
                                <i className="bi bi-box-seam"></i>My Products
                            </button>
                            <button type="button" className="fk-dropdown-item" role="menuitem"
                                onClick={() => go(`/seller/${token}/orders`)}>
                                <i className="bi bi-receipt"></i>Orders
                            </button>
                            <button type="button" className="fk-dropdown-item" role="menuitem"
                                onClick={() => go(`/seller/${token}/sales`)}>
                                <i className="bi bi-graph-up"></i>Sales Report
                            </button>
                        </>
                    )}
                    {role === 'ROLE_ADMIN' && (
                        <>
                            <button type="button" className="fk-dropdown-item" role="menuitem"
                                onClick={() => go(`/admin/${token}`)}>
                                <i className="bi bi-speedometer2"></i>Analytics
                            </button>
                            <button type="button" className="fk-dropdown-item" role="menuitem"
                                onClick={() => go(`/admin/${token}/users`)}>
                                <i className="bi bi-people"></i>Users
                            </button>
                            <button type="button" className="fk-dropdown-item" role="menuitem"
                                onClick={() => go(`/admin/${token}/categories`)}>
                                <i className="bi bi-tags"></i>Categories
                            </button>
                        </>
                    )}

                    <div className="fk-dropdown-separator"></div>
                    <button type="button" className="fk-dropdown-item fk-dropdown-item-danger" role="menuitem"
                        onClick={() => logout()}>
                        <i className="bi bi-box-arrow-right"></i>Logout
                    </button>
                </div>
            )}
        </div>
    )
}

export default ProfileDropdown
