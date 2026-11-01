import { useNavigate } from "react-router"
import CartList from "./CartList"
import Stat from "./Stat"
import { useEffect } from "react"
import { useDispatch } from "react-redux"
import { getCartItems } from "../../store/actions/CustomerAction"
import AddToCart from "./AddToCart"

function CustomerDashboard(){
    const navigate = useNavigate()
    const dispatch = useDispatch()
    const logout = () => {
        localStorage.clear()
        navigate("/")
    }

     useEffect(() => {
        dispatch(getCartItems(localStorage.getItem('id')))
    }, [])

    return(
        <div>
            <h1>CustomerDashboard</h1>
             <button className="btn btn-secondary" onClick={() => logout()}>Logout</button>
             <hr />
             <Stat />
             <hr />
             <AddToCart />
             <hr />
             <CartList />
        </div>
    )
}

export default CustomerDashboard
