import { useState } from "react"
import { addToCart } from "../../store/actions/CustomerAction"
import { useDispatch } from "react-redux"

function AddToCart() {
   const [productId, setProductId] = useState("")
   const [quantity, setQuantity] = useState("")

   const dispatch = useDispatch()

   const add = (e)=>{
        e.preventDefault()
        dispatch(addToCart(localStorage.getItem('id'), productId, quantity))
        
   }
    return (
        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
            <div className="card-header bg-white" style={{ borderBottom: '2px solid #2874F0' }}>
                <h5 className="mb-0 fw-semibold">Add Product to Cart</h5>
            </div>
            <div className="card-body">
                <form onSubmit={(e)=> add(e)}>
                    <div className="row g-3 align-items-end">
                        <div className="col-md-5">
                            <label className="form-label small fw-semibold text-secondary">Product Id</label>
                            <input type="text" className="form-control"
                            onChange={(e)=> setProductId(e.target.value)}/>
                        </div>
                        <div className="col-md-3">
                            <label className="form-label small fw-semibold text-secondary">Quantity</label>
                            <input type="text" className="form-control"
                             onChange={(e)=> setQuantity(e.target.value)}/>
                        </div>
                        <div className="col-md-4">
                            <button type="submit" className="btn fw-bold text-white w-100"
                            style={{ backgroundColor: '#FB641B', borderRadius: 2 }}>
                            +Add to Cart
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default AddToCart
