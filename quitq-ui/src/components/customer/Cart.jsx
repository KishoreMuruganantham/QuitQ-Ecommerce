import { useEffect, useState } from "react"
import { useDispatch, useSelector } from "react-redux"
import { Link, useNavigate, useParams } from "react-router"
import { deleteCartItem, getCartItems, updateQuantity } from "../../store/actions/CustomerAction"

function Cart() {
    const { token } = useParams()
    const navigate = useNavigate()
    const dispatch = useDispatch()
    const list = useSelector(state => state.cartItems)
    const [loading, setLoading] = useState(true)
    const [cartErr, setCartErr] = useState("")

    // load the cart of the logged in customer on mount
    useEffect(() => {
        const loadCart = async () => {
            await dispatch(getCartItems(localStorage.getItem('id')))
            setLoading(false)
        }
        loadCart()
    }, [dispatch])

    // price details math carried over from the old Stat component
    const totalQuantity = list.reduce((sum, c) => sum + c.quantity, 0)
    const totalAmount = list.reduce((sum, c) => sum + c.lineTotal, 0)

    const changeQty = async (item, newQty) => {
        const result = await dispatch(updateQuantity(item.cartItemId, newQty))
        if (result && result.ok) {
            setCartErr("")
        }
        else {
            setCartErr(result?.message || "Something went wrong")
        }
    }

    const removeItem = async (cartItemId) => {
        if (window.confirm('Remove this item from your cart?')) {
            const result = await dispatch(deleteCartItem(cartItemId))
            if (result && result.ok) {
                setCartErr("")
            }
            else {
                setCartErr(result?.message || "Something went wrong")
            }
        }
    }

    const placeOrder = () => {
        navigate(`/customer/${token}/checkout`)
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
            <div className="row g-3">

                {/* <!-- Cart items --> */}
                <div className="col-lg-8">
                    <div className="fk-card">
                        <div className="fk-card-header">My Cart ({list.length} items)</div>

                        {cartErr && <p className="fk-error px-3 pt-3">{cartErr}</p>}

                        {loading && (
                            <div className="fk-empty">
                                <i className="bi bi-hourglass-split"></i>
                                <p className="mb-0">Loading your cart...</p>
                            </div>
                        )}

                        {!loading && list.length === 0 && (
                            <div className="fk-empty">
                                <i className="bi bi-cart-x"></i>
                                <p className="mb-3">Your cart is empty</p>
                                <Link to={`/customer/${token}`} className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>
                                    Continue Shopping
                                </Link>
                            </div>
                        )}

                        {!loading && list.length > 0 && list.map(item => (
                            <div key={item.cartItemId} className="p-3 border-bottom">

                                {/* <!-- product + line total --> */}
                                <div className="d-flex justify-content-between">
                                    <div>
                                        <div className="fw-semibold">{item.productName}</div>
                                        <div className="fk-muted" style={{ fontSize: 13 }}>
                                            Rs. {Number(item.price).toLocaleString('en-IN')}
                                        </div>
                                    </div>
                                    <div className="text-end fw-semibold">
                                        Rs. {Number(item.lineTotal).toLocaleString('en-IN')}
                                    </div>
                                </div>

                                {/* <!-- quantity controls + remove --> */}
                                <div className="d-flex align-items-center gap-2 mt-3">
                                    <button type="button" className="fk-qty-btn" title="Decrease quantity"
                                        disabled={item.quantity <= 1}
                                        onClick={() => changeQty(item, item.quantity - 1)}>&minus;</button>
                                    <span className="fk-qty-val">{item.quantity}</span>
                                    <button type="button" className="fk-qty-btn" title="Increase quantity"
                                        disabled={item.quantity >= 10}
                                        onClick={() => changeQty(item, item.quantity + 1)}>+</button>

                                    <button type="button" className="btn btn-link btn-sm p-0 ms-auto fw-semibold"
                                        style={{ color: '#2874F0', textDecoration: 'none' }}
                                        onClick={() => removeItem(item.cartItemId)}>
                                        <i className="bi bi-trash3"></i> REMOVE
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* <!-- Price details --> */}
                <div className="col-lg-4">
                    <div className="fk-card">
                        <div className="fk-card-header">PRICE DETAILS</div>
                        <div className="p-3">
                            <div className="d-flex justify-content-between mb-2">
                                <span className="fk-muted">Price ({list.length} item{list.length !== 1 ? 's' : ''})</span>
                                <span>Rs. {Number(totalAmount).toLocaleString('en-IN')}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span className="fk-muted">Total Units</span>
                                <span>{totalQuantity}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span className="fk-muted">Delivery Charges</span>
                                <span style={{ color: '#388E3C' }}>FREE</span>
                            </div>
                            <hr className="my-2" />
                            <div className="d-flex justify-content-between mb-2">
                                <span className="fw-bold">Total Amount</span>
                                <span className="fw-bold" style={{ color: '#2874F0' }}>
                                    Rs. {Number(totalAmount).toLocaleString('en-IN')}
                                </span>
                            </div>
                            <hr className="my-2" />
                            <button type="button" className="fk-btn-primary mt-3"
                                disabled={loading || list.length === 0}
                                onClick={placeOrder}>
                                PLACE ORDER
                            </button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    )
}

export default Cart
