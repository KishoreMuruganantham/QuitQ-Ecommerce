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
        <div style={{ backgroundColor: '#f1f3f6', minHeight: '100vh' }}>
            {/* <!-- Top navigation bar --> */}
            <nav className="navbar navbar-expand-lg text-white" style={{ backgroundColor: '#2874F0' }}>
                <div className="container">
                    <span className="navbar-brand fw-bold text-white">QuitQ</span>
                    <span className="navbar-text me-3 text-white-50 d-none d-sm-inline">
                        Hi, {localStorage.getItem('username')}
                    </span>
                    <button className="btn btn-light btn-sm fw-semibold" onClick={() => logout()}>Logout</button>
                </div>
            </nav>

            <div className="container py-4">
                {/* <!-- Page heading --> */}
                <h4 className="mb-3 fw-semibold">Customer Dashboard</h4>

                <div className="row g-4">
                    {/* <!-- Left: cart table + add form --> */}
                    <div className="col-lg-8">
                        <CartList />
                        <div className="mt-4">
                            <AddToCart />
                        </div>
                    </div>

                    {/* <!-- Right: price details --> */}
                    <div className="col-lg-4">
                        <Stat />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default CustomerDashboard
