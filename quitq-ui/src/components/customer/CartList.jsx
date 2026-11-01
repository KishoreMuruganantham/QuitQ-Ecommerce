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
        <div>
            {
                list?.map((c, index)=>(
                    <div key={index}>
                        <p>{c.productName} &nbsp;&nbsp;&nbsp; 
                            <button onClick={()=>onDelete(c.cartItemId)}>delete</button>
                        </p>
                    </div>
                ))
            }
        </div>
    )
}

export default CartList
