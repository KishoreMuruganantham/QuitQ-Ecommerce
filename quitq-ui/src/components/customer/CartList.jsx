import { useEffect } from "react"
import { deleteCartItem, getCartItems } from "../../store/actions/CustomerAction"
import { useDispatch, useSelector } from "react-redux"

function CartList() {
    const list = useSelector(state=>state.cartItems)
    const dispatch = useDispatch()
    const onDelete = (id)=>{
        dispatch(deleteCartItem(id))
    }
    return (
        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
            <div className="card-header bg-white" style={{ borderBottom: '2px solid #2874F0' }}>
                <h5 className="mb-0 fw-semibold">My Cart ({list?.length})</h5>
            </div>
            <div className="card-body p-0">
                <table className="table table-hover align-middle mb-0">
                    <thead style={{ backgroundColor: '#f1f3f6' }}>
                        <tr>
                            <th style={{ width: 50 }}>#</th>
                            <th>Product</th>
                            <th className="text-center">Quantity</th>
                            <th className="text-end">Price</th>
                            <th className="text-end">Line Total</th>
                            <th className="text-center">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            list?.map((c, index)=>(
                                <tr key={index}>
                                    <td className="text-muted">{index + 1}</td>
                                    <td className="fw-semibold">{c.productName}</td>
                                    <td className="text-center">{c.quantity}</td>
                                    <td className="text-end">Rs. {c.lineTotal && c.quantity ? (c.lineTotal / c.quantity).toFixed(2) : '-'}</td>
                                    <td className="text-end fw-semibold">Rs. {c.lineTotal}</td>
                                    <td className="text-center">
                                        <button className="btn btn-sm btn-outline-danger" onClick={()=>onDelete(c.cartItemId)}>delete</button>
                                    </td>
                                </tr>
                            ))
                        }
                        {
                            (!list || list.length === 0) && (
                                <tr>
                                    <td colSpan="6" className="text-center text-muted py-4">No items in the cart yet</td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default CartList
