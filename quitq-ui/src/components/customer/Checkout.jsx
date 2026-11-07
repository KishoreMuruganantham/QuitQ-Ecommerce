import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"
import { getCartItems } from "../../store/actions/CustomerAction"

const emptyAddress = {
    recipientName: '', phone: '', streetAddress: '', city: '', state: '', postalCode: '', isDefault: false
}

const paymentMethods = [
    { value: 'COD', icon: 'bi-cash-coin', label: 'COD', desc: 'Pay on delivery' },
    { value: 'UPI', icon: 'bi-upc-scan', label: 'UPI', desc: 'Scan & pay with any UPI app' },
    { value: 'CREDIT_CARD', icon: 'bi-credit-card', label: 'Credit Card', desc: 'Visa, MasterCard, RuPay' },
    { value: 'DEBIT_CARD', icon: 'bi-credit-card-2-front', label: 'Debit Card', desc: 'Pay directly from your bank' },
    { value: 'NET_BANKING', icon: 'bi-bank', label: 'Net Banking', desc: 'All major banks supported' }
]

function Checkout() {
    const { token } = useParams()
    const navigate = useNavigate()
    const dispatch = useDispatch()
    const list = useSelector(state => state.cartItems)

    const [loading, setLoading] = useState(true)
    const [addresses, setAddresses] = useState([])
    const [selectedAddressId, setSelectedAddressId] = useState(null)
    const [showForm, setShowForm] = useState(false)
    const [form, setForm] = useState({ ...emptyAddress })
    const [addressErr, setAddressErr] = useState("")
    const [savingAddress, setSavingAddress] = useState(false)
    const [method, setMethod] = useState('COD')
    const [placing, setPlacing] = useState(false)
    const [orderErr, setOrderErr] = useState("")

    const authHeaders = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }

    // load cart + saved addresses on mount
    useEffect(() => {
        const loadCheckout = async () => {
            await dispatch(getCartItems(localStorage.getItem('id')))
            try {
                const response = await axios.get(
                    `${API_SERVER_URL_DEV}/api/address/all/${localStorage.getItem('id')}`,
                    { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') } })
                setAddresses(response.data)
                const defaultAddress = response.data.find(a => a.isDefault)
                if (defaultAddress) setSelectedAddressId(defaultAddress.id)
            }
            catch {
                setAddresses([])
            }
            setLoading(false)
        }
        loadCheckout()
    }, [dispatch])

    const totalAmount = list.reduce((sum, c) => sum + c.lineTotal, 0)

    const onField = (e) => {
        const { name, value } = e.target
        setForm({ ...form, [name]: value })
    }

    const closeForm = () => {
        setForm({ ...emptyAddress })
        setAddressErr("")
        setShowForm(false)
    }

    // save the new address, refresh the list and select it
    const saveAddress = async (e) => {
        e.preventDefault()
        setAddressErr("")
        setSavingAddress(true)
        try {
            const response = await axios.post(
                `${API_SERVER_URL_DEV}/api/address/add/${localStorage.getItem('id')}`,
                form, { headers: authHeaders })
            const refreshed = await axios.get(
                `${API_SERVER_URL_DEV}/api/address/all/${localStorage.getItem('id')}`,
                { headers: authHeaders })
            setAddresses(refreshed.data)
            setSelectedAddressId(response.data.id)
            setForm({ ...emptyAddress })
            setShowForm(false)
        }
        catch (err) {
            setAddressErr(err?.response?.data?.message || 'Unable to save the address')
        }
        finally {
            setSavingAddress(false)
        }
    }

    const placeOrder = async () => {
        if (!selectedAddressId) return
        setOrderErr("")
        setPlacing(true)
        try {
            const response = await axios.post(
                `${API_SERVER_URL_DEV}/api/order/checkout/${localStorage.getItem('id')}/${selectedAddressId}?method=${method}`,
                null, { headers: authHeaders })
            const order = response.data

            // checkout only records the payment as PENDING -> complete it for online methods
            if (method !== 'COD') {
                try {
                    const txnId = 'TXN' + Date.now()
                    await axios.post(
                        `${API_SERVER_URL_DEV}/api/payment/${order.orderId}/complete?txnId=${txnId}`,
                        null, { headers: authHeaders })
                }
                catch {
                    /* the order is already placed, so continue even if this follow up fails */
                }
            }

            await dispatch(getCartItems(localStorage.getItem('id')))
            navigate(`/customer/${token}/order-confirmation/${order.orderId}`, { replace: true })
        }
        catch (err) {
            setOrderErr(err?.response?.data?.message || 'Unable to place the order. Please try again.')
        }
        finally {
            setPlacing(false)
        }
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Loading state --> */}
            {loading && (
                <div className="fk-card fk-empty">
                    <i className="bi bi-hourglass-split"></i>
                    <p className="mb-0">Loading checkout...</p>
                </div>
            )}

            {/* <!-- Nothing to buy -> back to cart --> */}
            {!loading && list.length === 0 && (
                <div className="fk-card fk-empty">
                    <i className="bi bi-cart-x"></i>
                    <p className="mb-3">No items to checkout</p>
                    <Link to={`/customer/${token}/cart`} className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>
                        Go to Cart
                    </Link>
                </div>
            )}

            {!loading && list.length > 0 && (
                <div className="row g-3">

                    {/* <!-- Delivery address --> */}
                    <div className="col-lg-7">
                        <div className="fk-card">
                            <div className="fk-card-header">DELIVERY ADDRESS</div>
                            <div className="p-3">

                                {addresses.length === 0 && !showForm && (
                                    <p className="fk-muted mb-3">No saved addresses yet. Add one to continue.</p>
                                )}

                                {addresses.map(a => (
                                    <label key={a.id} className="d-block p-3 mb-2"
                                        style={{
                                            border: selectedAddressId === a.id ? '1px solid #2874F0' : '1px solid #e0e0e0',
                                            background: selectedAddressId === a.id ? '#f0f6ff' : '#fff',
                                            borderRadius: 2, cursor: 'pointer'
                                        }}>
                                        <div className="d-flex gap-2">
                                            <input type="radio" name="deliveryAddress" className="mt-1"
                                                checked={selectedAddressId === a.id}
                                                onChange={() => setSelectedAddressId(a.id)} />
                                            <div>
                                                <div className="d-flex align-items-center gap-2 flex-wrap">
                                                    <span className="fw-semibold">{a.recipientName}</span>
                                                    <span className="fk-muted" style={{ fontSize: 13 }}>{a.phone}</span>
                                                    {a.isDefault && <span className="fk-badge fk-badge-blue">Default</span>}
                                                </div>
                                                <div className="mt-1" style={{ fontSize: 14 }}>{a.streetAddress}</div>
                                                <div className="fk-muted" style={{ fontSize: 14 }}>
                                                    {a.city}, {a.state} - {a.postalCode}
                                                </div>
                                            </div>
                                        </div>
                                    </label>
                                ))}

                                {/* <!-- Toggle inline address form --> */}
                                {!showForm && (
                                    <button type="button" className="fk-btn-outline mt-2" onClick={() => setShowForm(true)}>
                                        <i className="bi bi-plus-lg"></i> ADD NEW ADDRESS
                                    </button>
                                )}

                                {showForm && (
                                    <form className="mt-3 p-3" style={{ border: '1px solid #e0e0e0', borderRadius: 2 }}
                                        onSubmit={(e) => saveAddress(e)}>
                                        {addressErr && <p className="fk-error mb-2">{addressErr}</p>}
                                        <div className="row g-3">
                                            <div className="col-md-6">
                                                <label className="fk-label" htmlFor="recipientName">Recipient Name</label>
                                                <input id="recipientName" name="recipientName" type="text" className="fk-input"
                                                    required value={form.recipientName} onChange={(e) => onField(e)} />
                                            </div>
                                            <div className="col-md-6">
                                                <label className="fk-label" htmlFor="phone">Phone</label>
                                                <input id="phone" name="phone" type="text" className="fk-input"
                                                    required value={form.phone} onChange={(e) => onField(e)} />
                                            </div>
                                            <div className="col-12">
                                                <label className="fk-label" htmlFor="streetAddress">Street Address</label>
                                                <input id="streetAddress" name="streetAddress" type="text" className="fk-input"
                                                    required value={form.streetAddress} onChange={(e) => onField(e)} />
                                            </div>
                                            <div className="col-md-4">
                                                <label className="fk-label" htmlFor="city">City</label>
                                                <input id="city" name="city" type="text" className="fk-input"
                                                    required value={form.city} onChange={(e) => onField(e)} />
                                            </div>
                                            <div className="col-md-4">
                                                <label className="fk-label" htmlFor="state">State</label>
                                                <input id="state" name="state" type="text" className="fk-input"
                                                    required value={form.state} onChange={(e) => onField(e)} />
                                            </div>
                                            <div className="col-md-4">
                                                <label className="fk-label" htmlFor="postalCode">Postal Code</label>
                                                <input id="postalCode" name="postalCode" type="text" className="fk-input"
                                                    required value={form.postalCode} onChange={(e) => onField(e)} />
                                            </div>
                                        </div>
                                        <div className="form-check mt-3">
                                            <input className="form-check-input" type="checkbox" id="isDefault"
                                                checked={form.isDefault}
                                                onChange={(e) => setForm({ ...form, isDefault: e.target.checked })} />
                                            <label className="form-check-label small fw-semibold text-secondary" htmlFor="isDefault">
                                                Set as default address
                                            </label>
                                        </div>
                                        <div className="d-flex gap-2 mt-3">
                                            <button type="submit" className="fk-btn-blue" disabled={savingAddress}>
                                                {savingAddress ? 'SAVING...' : 'SAVE ADDRESS'}
                                            </button>
                                            <button type="button" className="fk-btn-outline" onClick={closeForm}>CANCEL</button>
                                        </div>
                                    </form>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* <!-- Order summary + payment --> */}
                    <div className="col-lg-5">

                        <div className="fk-card mb-3">
                            <div className="fk-card-header">ORDER SUMMARY</div>
                            <div className="p-3">
                                {list.map(item => (
                                    <div key={item.cartItemId} className="d-flex justify-content-between mb-2">
                                        <span>{item.productName} &times; {item.quantity}</span>
                                        <span className="fw-semibold">
                                            Rs. {Number(item.lineTotal).toLocaleString('en-IN')}
                                        </span>
                                    </div>
                                ))}
                                <hr className="my-2" />
                                <div className="d-flex justify-content-between">
                                    <span className="fw-bold">Total Amount</span>
                                    <span className="fw-bold" style={{ color: '#2874F0' }}>
                                        Rs. {Number(totalAmount).toLocaleString('en-IN')}
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div className="fk-card mb-3">
                            <div className="fk-card-header">PAYMENT OPTIONS</div>
                            <div className="p-3">
                                {paymentMethods.map(m => (
                                    <label key={m.value} className="d-flex align-items-center gap-2 p-2 mb-1"
                                        style={{
                                            border: method === m.value ? '1px solid #2874F0' : '1px solid #e0e0e0',
                                            background: method === m.value ? '#f0f6ff' : '#fff',
                                            borderRadius: 2, cursor: 'pointer'
                                        }}>
                                        <input type="radio" name="paymentMethod"
                                            checked={method === m.value}
                                            onChange={() => setMethod(m.value)} />
                                        <i className={`bi ${m.icon}`} style={{ fontSize: 18, color: '#2874F0' }}></i>
                                        <div>
                                            <div className="fw-semibold" style={{ fontSize: 14 }}>{m.label}</div>
                                            <div className="fk-muted" style={{ fontSize: 12 }}>{m.desc}</div>
                                        </div>
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div className="fk-card p-3">
                            {/* <!-- backend errors (invalid address, empty cart, stock...) --> */}
                            {orderErr && <p className="fk-error mb-2">{orderErr}</p>}
                            <button type="button" className="fk-btn-primary"
                                disabled={!selectedAddressId || placing}
                                onClick={placeOrder}>
                                {placing ? 'PLACING ORDER...' : 'PLACE ORDER'}
                            </button>
                            {!selectedAddressId && (
                                <p className="fk-muted small mt-2 mb-0">Select a delivery address to continue.</p>
                            )}
                        </div>

                    </div>
                </div>
            )}
        </div>
    )
}

export default Checkout
