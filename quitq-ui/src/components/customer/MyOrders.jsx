import { useEffect, useState } from "react"
import { Link, useNavigate, useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function MyOrders() {
    const { token } = useParams()
    const navigate = useNavigate()
    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(true)
    const [errMsg, setErrMsg] = useState("")

    // fetch all orders of the logged in customer on mount
    useEffect(() => {
        const loadOrders = async () => {
            try {
                const response = await axios.get(
                    `${API_SERVER_URL_DEV}/api/order/customer/${localStorage.getItem('id')}?size=100`,
                    {
                        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                    })
                setOrders(response.data)
            }
            catch (err) {
                setErrMsg(err?.response?.data?.message || 'Unable to load your orders')
            }
            finally {
                setLoading(false)
            }
        }
        loadOrders()
    }, [])

    const badgeClass = (status) => {
        if (status === 'PROCESSING') return 'fk-badge fk-badge-orange'
        if (status === 'SHIPPED') return 'fk-badge fk-badge-blue'
        if (status === 'DELIVERED') return 'fk-badge fk-badge-green'
        if (status === 'CANCELLED') return 'fk-badge fk-badge-red'
        return 'fk-badge fk-badge-gray'
    }

    const openTracking = (orderId) => {
        navigate(`/customer/${token}/orders/${orderId}`)
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
            <div className="fk-card">

                <div className="fk-card-header">My Orders ({orders.length})</div>

                {/* <!-- Loading state --> */}
                {loading && (
                    <div className="fk-empty">
                        <i className="bi bi-hourglass-split"></i>
                        <p className="mb-0">Loading your orders...</p>
                    </div>
                )}

                {/* <!-- Error state --> */}
                {!loading && errMsg && (
                    <div className="fk-empty">
                        <i className="bi bi-exclamation-triangle"></i>
                        <p className="mb-0">{errMsg}</p>
                    </div>
                )}

                {/* <!-- Empty state --> */}
                {!loading && !errMsg && orders.length === 0 && (
                    <div className="fk-empty">
                        <i className="bi bi-box-seam"></i>
                        <p className="mb-3">No orders yet</p>
                        <Link to={`/customer/${token}`} className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>
                            Continue Shopping
                        </Link>
                    </div>
                )}

                {/* <!-- Orders table --> */}
                {!loading && !errMsg && orders.length > 0 && (
                    <div className="table-responsive">
                        <table className="fk-table">
                            <thead>
                                <tr>
                                    <th>Order No</th>
                                    <th>Date</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {orders.map(o => (
                                    <tr key={o.orderId} style={{ cursor: 'pointer' }}
                                        onClick={() => openTracking(o.orderId)}>
                                        <td className="fw-semibold">{o.orderNumber}</td>
                                        <td className="fk-muted">{new Date(o.orderDate).toLocaleString()}</td>
                                        <td className="fw-semibold">Rs. {Number(o.totalAmount).toLocaleString('en-IN')}</td>
                                        <td><span className={badgeClass(o.orderStatus)}>{o.orderStatus}</span></td>
                                        <td>
                                            {/* <!-- link to the tracking page, row click also opens it --> */}
                                            <Link to={`/customer/${token}/orders/${o.orderId}`}
                                                className="fw-semibold text-decoration-none"
                                                style={{ color: '#2874F0' }}
                                                onClick={(e) => e.stopPropagation()}>
                                                Track
                                            </Link>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}

export default MyOrders
