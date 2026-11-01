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
        <div className="container">
            <h3>Add Product to Cart</h3>
            <form onSubmit={(e)=> add(e)}>
                <div className="row">
                    <div className="col-md-6">
                        <div className="mt-4">
                            <label>Product Id</label>
                            <input type="text" className="form-control"
                            onChange={(e)=> setProductId(e.target.value)}/>
                        </div>
                        <div className="mt-4">
                            <label>Quantity</label>
                            <input type="text" className="form-control"
                             onChange={(e)=> setQuantity(e.target.value)}/>
                        </div>
                        <div className="mt-4">
                             
                            <button type="submit" className="btn btn-primary" >
                            +Add to Cart
                            </button>
                        </div>
                    </div>

                </div>
            </form>
        </div>
    )
}

export default AddToCart
