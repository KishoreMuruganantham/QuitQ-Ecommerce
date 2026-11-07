import { useEffect, useState } from "react"
import { useNavigate, useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

// Order in which the statuses are shown in the table
const STATUS_ORDER = ["PLACED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"]

// Badge colour for each order status
const STATUS_BADGE = {
    PLACED: "fk-badge-gray",
    PROCESSING: "fk-badge-orange",
    SHIPPED: "fk-badge-blue",
    DELIVERED: "fk-badge-green",
    CANCELLED: "fk-badge-red",
}

function Analytics() {
    const { token } = useParams()
    const navigate = useNavigate()

    const [userCount, setUserCount] = useState(0)
    const [sellerCount, setSellerCount] = useState(0)
    const [orderStats, setOrderStats] = useState([])
    const [loading, setLoading] = useState(true)
    const [errMsg, setErrMsg] = useState("")
    // Bumped by the RETRY button so the stats are fetched again
    const [refreshKey, setRefreshKey] = useState(0)

    // Load counts + order status stats in one go
    useEffect(() => {
        const loadAnalytics = async () => {
            const authHeaders = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
            setLoading(true)
            setErrMsg("")
            try {
                const [usersResp, sellersResp, ordersResp] = await Promise.all([
                    axios.get(API_SERVER_URL_DEV + '/api/admin/users/count', { headers: authHeaders }),
                    axios.get(API_SERVER_URL_DEV + '/api/admin/sellers/count', { headers: authHeaders }),
                    axios.get(API_SERVER_URL_DEV + '/api/admin/analytics/orders-by-status', { headers: authHeaders }),
                ])
                setUserCount(usersResp.data)
                setSellerCount(sellersResp.data)
                setOrderStats(ordersResp.data)
            }
            catch (err) {
                setErrMsg(err?.response?.data?.message || "Unable to load analytics...")
            }
            finally {
                setLoading(false)
            }
        }
        loadAnalytics()
    }, [refreshKey])

    // Orders across every status
    const totalOrders = orderStats.reduce((sum, s) => sum + (s.numberOfOrders || 0), 0)

    // Keep the rows in the fixed enum order
    const sortedStats = [...orderStats].sort(
        (a, b) => STATUS_ORDER.indexOf(a.orderStatus) - STATUS_ORDER.indexOf(b.orderStatus)
    )

    if (loading) {
        return (
            <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
                <div className="fk-card fk-empty">Loading...</div>
            </div>
        )
    }

    if (errMsg) {
        return (
            <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
                <div className="fk-card fk-empty">
                    <i className="bi bi-exclamation-triangle"></i>
                    <p className="mb-3">{errMsg}</p>
                    <button type="button" className="fk-btn-outline w-auto"
                        onClick={() => setRefreshKey((key) => key + 1)}>
                        RETRY
                    </button>
                </div>
            </div>
        )
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Summary stat cards --> */}
            <div className="row row-cols-1 row-cols-md-3 g-3">
                <div className="col">
                    <div className="fk-card p-3 d-flex align-items-center gap-3">
                        <i className="bi bi-people-fill fs-2" style={{ color: '#2874F0' }}></i>
                        <div>
                            <div className="fk-muted" style={{ fontSize: 13 }}>Users</div>
                            <h3 className="fw-bold mb-0">{userCount}</h3>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="fk-card p-3 d-flex align-items-center gap-3">
                        <i className="bi bi-shop fs-2" style={{ color: '#FB641B' }}></i>
                        <div>
                            <div className="fk-muted" style={{ fontSize: 13 }}>Sellers</div>
                            <h3 className="fw-bold mb-0">{sellerCount}</h3>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="fk-card p-3 d-flex align-items-center gap-3">
                        <i className="bi bi-receipt fs-2" style={{ color: '#388E3C' }}></i>
                        <div>
                            <div className="fk-muted" style={{ fontSize: 13 }}>Orders</div>
                            <h3 className="fw-bold mb-0">{totalOrders}</h3>
                        </div>
                    </div>
                </div>
            </div>

            {/* <!-- Orders by status breakdown --> */}
            <div className="fk-card mt-3">
                <div className="fk-card-header">Orders by Status</div>
                <table className="fk-table">
                    <thead>
                        <tr>
                            <th>Status</th>
                            <th>Number of Orders</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            sortedStats.map((s) => (
                                <tr key={s.orderStatus}>
                                    <td>
                                        <span className={'fk-badge ' + (STATUS_BADGE[s.orderStatus] || "fk-badge-gray")}>
                                            {s.orderStatus}
                                        </span>
                                    </td>
                                    <td className="fw-semibold">{s.numberOfOrders}</td>
                                </tr>
                            ))
                        }
                        {
                            sortedStats.length === 0 && (
                                <tr>
                                    <td colSpan="2" className="fk-muted text-center py-4">No orders yet</td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
            </div>

            {/* <!-- Quick links to the manage pages --> */}
            <div className="row row-cols-1 row-cols-md-2 g-3 mt-1">
                <div className="col">
                    <button className="fk-btn-outline" style={{ width: 'auto' }}
                        onClick={() => navigate(`/admin/${token}/users`)}>
                        MANAGE USERS
                    </button>
                </div>
                <div className="col">
                    <button className="fk-btn-blue" style={{ width: 'auto' }}
                        onClick={() => navigate(`/admin/${token}/categories`)}>
                        MANAGE CATEGORIES
                    </button>
                </div>
            </div>

        </div>
    )
}

export default Analytics
