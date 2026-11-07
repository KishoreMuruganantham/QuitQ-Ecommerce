import { useState, useEffect } from "react"
import { useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

// The five order statuses supported by the backend OrderStatus enum
const ORDER_STATUSES = ["PLACED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"]

function SellerOrders() {
    const { token } = useParams()
    const bearer = localStorage.getItem('token') || token
    const authHeaders = { headers: { 'Authorization': 'Bearer ' + bearer } }

    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(true)
    const [listErr, setListErr] = useState("")
    const [statusErr, setStatusErr] = useState("")
    // Bumped when a status update fails so the selects remount and revert
    const [selectRev, setSelectRev] = useState(0)

    useEffect(() => {
        const headers = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
        const loadOrders = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/order/seller/' + localStorage.getItem('id') + '?size=100',
                    { headers: headers })
                setOrders(resp.data)
                setListErr("")
            }
            catch (err) {
                setListErr(err?.response?.data?.message || "Unable to load orders...")
            }
            finally {
                setLoading(false)
            }
        }
        loadOrders()
    }, [])

    // PUT the new status for one order item, then replace the row with the response
    const changeStatus = async (item, newStatus) => {
        setStatusErr("")
        try {
            const resp = await axios.put(
                API_SERVER_URL_DEV + '/api/order/item/' + item.id + '/status?status=' + newStatus,
                null, authHeaders)
            setOrders((prev) => prev.map((order) => (order.id === item.id ? resp.data : order)))
        }
        catch (err) {
            setStatusErr(err?.response?.data?.message || "Unable to update status...")
            // Remount the selects so they snap back to the stored status
            setSelectRev((rev) => rev + 1)
        }
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
            <div className="fk-card">
                <div className="fk-card-header">Orders ({orders.length})</div>

                {statusErr && <div className="px-3 pt-3"><p className="fk-error">{statusErr}</p></div>}

                {loading && (
                    <div className="fk-empty">
                        <i className="bi bi-hourglass-split"></i>
                        Loading orders...
                    </div>
                )}

                {!loading && listErr && (
                    <div className="p-3"><p className="fk-error">{listErr}</p></div>
                )}

                {!loading && !listErr && orders.length === 0 && (
                    <div className="fk-empty">
                        <i className="bi bi-receipt"></i>
                        No orders received yet
                    </div>
                )}

                {!loading && !listErr && orders.length > 0 && (
                    <table className="fk-table">
                        <thead>
                            <tr>
                                <th>ORDER NO</th>
                                <th>CUSTOMER</th>
                                <th>PRODUCT</th>
                                <th>QTY</th>
                                <th>AMOUNT</th>
                                <th>STATUS</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map((item) => (
                                <tr key={item.id}>
                                    <td className="fw-semibold">{item.orderNumber}</td>
                                    <td>{item.customerName}</td>
                                    <td>{item.productName}</td>
                                    <td>{item.quantity}</td>
                                    <td>Rs. {Number(item.totalPrice ?? 0).toLocaleString('en-IN')}</td>
                                    <td>
                                        <select className="form-select form-select-sm"
                                            key={item.id + '-' + selectRev}
                                            value={item.status}
                                            onChange={(e) => changeStatus(item, e.target.value)}>
                                            {ORDER_STATUSES.map((status) => (
                                                <option key={status} value={status}>{status}</option>
                                            ))}
                                        </select>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    )
}

export default SellerOrders
