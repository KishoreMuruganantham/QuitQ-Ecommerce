import { useEffect, useState } from "react"
import { Link, useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

const FLOW = ['PLACED', 'PROCESSING', 'SHIPPED', 'DELIVERED']

function OrderTracking() {
    const { token, id } = useParams()
    const [order, setOrder] = useState(null)
    const [tracking, setTracking] = useState([])
    const [loading, setLoading] = useState(true)
    const [errMsg, setErrMsg] = useState("")

    // fetch the order and its tracking history on mount
    useEffect(() => {
        const loadOrder = async () => {
            try {
                const response = await axios.get(`${API_SERVER_URL_DEV}/api/order/${id}`,
                    {
                        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                    })
                setOrder(response.data)
            }
            catch (err) {
                setErrMsg(err?.response?.data?.message || 'Unable to load this order')
            }

            try {
                const response = await axios.get(`${API_SERVER_URL_DEV}/api/order/${id}/tracking`,
                    {
                        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                    })
                setTracking(response.data)
            }
            catch {
                setTracking([])
            }
            setLoading(false)
        }
        loadOrder()
    }, [id])

    const badgeClass = (status) => {
        if (status === 'PROCESSING') return 'fk-badge fk-badge-orange'
        if (status === 'SHIPPED') return 'fk-badge fk-badge-blue'
        if (status === 'DELIVERED') return 'fk-badge fk-badge-green'
        if (status === 'CANCELLED') return 'fk-badge fk-badge-red'
        return 'fk-badge fk-badge-gray'
    }

    // highest step reached from the tracking entries, falling back to the order status
    const reachedIndex = () => {
        let max = -1
        tracking.forEach(t => {
            const i = FLOW.indexOf(String(t.status).toUpperCase())
            if (i > max) max = i
        })
        if (order) {
            const i = FLOW.indexOf(String(order.orderStatus).toUpperCase())
            if (i > max) max = i
        }
        return max
    }

    const entryFor = (step) => tracking.find(t => String(t.status).toUpperCase() === step)
    const step = reachedIndex()

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Back link --> */}
            <Link to={`/customer/${token}/orders`} className="fw-semibold text-decoration-none d-inline-block mb-3"
                style={{ color: '#2874F0' }}>
                <i className="bi bi-arrow-left"></i> Back to My Orders
            </Link>

            {/* <!-- Loading state --> */}
            {loading && (
                <div className="fk-card fk-empty">
                    <i className="bi bi-hourglass-split"></i>
                    <p className="mb-0">Loading order details...</p>
                </div>
            )}

            {/* <!-- Error state --> */}
            {!loading && !order && (
                <div className="fk-card fk-empty">
                    <i className="bi bi-exclamation-triangle"></i>
                    <p className="mb-3">{errMsg}</p>
                    <Link to={`/customer/${token}/orders`} className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>
                        Back to My Orders
                    </Link>
                </div>
            )}

            {!loading && order && (
                <div>

                    {/* <!-- Order header --> */}
                    <div className="fk-card p-3 mb-3">
                        <div className="d-flex justify-content-between align-items-start flex-wrap gap-2">
                            <div>
                                <div className="fk-section-title mb-1">Order {order.orderNumber}</div>
                                <div className="fk-muted" style={{ fontSize: 13 }}>
                                    {order.orderDate && new Date(order.orderDate).toLocaleString()}
                                </div>
                            </div>
                            <div className="text-end">
                                <div className="fw-semibold mb-1">Rs. {Number(order.totalAmount).toLocaleString('en-IN')}</div>
                                <span className={badgeClass(order.orderStatus)}>{order.orderStatus}</span>
                            </div>
                        </div>
                    </div>

                    {/* <!-- Cancelled orders do not show the timeline --> */}
                    {order.orderStatus === 'CANCELLED' && (
                        <div className="fk-card p-4 text-center" style={{ border: '1px solid #fdecea' }}>
                            <i className="bi bi-x-octagon-fill" style={{ fontSize: 48, color: '#d32f2f' }}></i>
                            <h5 className="mt-3 fw-semibold" style={{ color: '#d32f2f' }}>This order was cancelled</h5>
                            <p className="fk-muted mb-0">Contact support if you think this is a mistake.</p>
                        </div>
                    )}

                    {/* <!-- No tracking updates yet --> */}
                    {order.orderStatus !== 'CANCELLED' && tracking.length === 0 && (
                        <div className="fk-card p-4">
                            <span className={badgeClass(order.orderStatus)}>{order.orderStatus}</span>
                            <p className="fk-muted mt-3 mb-0">No tracking updates yet</p>
                        </div>
                    )}

                    {/* <!-- Status timeline --> */}
                    {order.orderStatus !== 'CANCELLED' && tracking.length > 0 && (
                        <div className="fk-card p-4">
                            <div className="fk-section-title">TRACKING</div>
                            <ul className="fk-timeline mt-3">
                                {FLOW.map((s, index) => {
                                    const done = index <= step
                                    const entry = entryFor(s)
                                    return (
                                        <li key={s} className={done ? 'fk-step-done' : ''}>
                                            <span className="fk-step-dot"></span>
                                            <div className="fw-semibold" style={done ? { color: '#388E3C' } : {}}>
                                                {s}
                                            </div>
                                            {entry ? (
                                                <div className="fk-muted" style={{ fontSize: 13 }}>
                                                    {entry.statusTime && new Date(entry.statusTime).toLocaleString()}
                                                    {entry.remarks && <div>{entry.remarks}</div>}
                                                </div>
                                            ) : (
                                                <div className="fk-muted" style={{ fontSize: 13 }}>Pending</div>
                                            )}
                                        </li>
                                    )
                                })}
                            </ul>
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}

export default OrderTracking
