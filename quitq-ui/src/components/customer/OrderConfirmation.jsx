import { useEffect, useState } from "react"
import { Link, useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function OrderConfirmation() {
    const { token, id } = useParams()
    const [order, setOrder] = useState(null)
    const [loading, setLoading] = useState(true)
    const [errMsg, setErrMsg] = useState("")

    // fetch the placed order on mount
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
            finally {
                setLoading(false)
            }
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

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Loading state --> */}
            {loading && (
                <div className="fk-card fk-empty mx-auto mt-5" style={{ maxWidth: 600 }}>
                    <i className="bi bi-hourglass-split"></i>
                    <p className="mb-0">Loading your order...</p>
                </div>
            )}

            {/* <!-- Error state --> */}
            {!loading && !order && (
                <div className="fk-card fk-empty mx-auto mt-5" style={{ maxWidth: 600 }}>
                    <i className="bi bi-exclamation-triangle"></i>
                    <p className="mb-3">{errMsg}</p>
                    <Link to={`/customer/${token}`} className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>
                        Back to Home
                    </Link>
                </div>
            )}

            {/* <!-- Success card --> */}
            {!loading && order && (
                <div className="fk-card p-5 text-center mx-auto mt-5" style={{ maxWidth: 600 }}>
                    <i className="bi bi-check-circle-fill text-success" style={{ fontSize: 64 }}></i>
                    <h4 className="mt-3 fw-semibold">Order Placed Successfully!</h4>
                    <p className="mb-1 fw-bold">{order.orderNumber}</p>
                    <p className="fk-price-lg mb-2">Rs. {Number(order.totalAmount).toLocaleString('en-IN')}</p>
                    <div className="mb-2">
                        <span className={badgeClass(order.orderStatus)}>{order.orderStatus}</span>
                    </div>
                    <p className="fk-muted" style={{ fontSize: 13 }}>
                        {order.orderDate && new Date(order.orderDate).toLocaleString()}
                    </p>

                    {/* <!-- next steps --> */}
                    <div className="d-flex gap-2 mt-4">
                        <Link to={`/customer/${token}`} className="fk-btn-primary text-center text-decoration-none">
                            CONTINUE SHOPPING
                        </Link>
                        <Link to={`/customer/${token}/orders`} className="fk-btn-outline text-center text-decoration-none">
                            VIEW MY ORDERS
                        </Link>
                    </div>
                </div>
            )}
        </div>
    )
}

export default OrderConfirmation
