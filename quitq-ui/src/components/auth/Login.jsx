import { useState } from "react"
import { Link, useNavigate } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config"
import axios from "axios"

function Login() {
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [errMsg, setErrMsg] = useState("")
    const [errComment, setErrComment] = useState("")

    const apiUrl = API_SERVER_URL_DEV + '/api/auth/login'
    const userDetailsUrl = API_SERVER_URL_DEV + '/api/auth/user-details'

    const navigate =  useNavigate()
    const processLogin = async (e) => {
        e.preventDefault()
        // Step 1. Fetch the jwt token
        const token = 'Basic ' + window.btoa(username + ':' + password)
        // console.log(token)
        try {
            const response = await axios.get(apiUrl, {
                headers: { 'Authorization': token }
            })

            // console.log(response)
            // console.log(response.data.token)
            localStorage.setItem('token', response.data.token)
            localStorage.setItem('username', username)

            // Step 2. Fetch User Details using jwtToken
            const resp = await axios.get(userDetailsUrl, {
                headers: { 'Authorization': 'Bearer ' + response.data.token }
            })
            // console.log(resp)
            localStorage.setItem('role', resp.data.role)
            localStorage.setItem('id', resp.data.id)
            // Based on the role of this user load appropriate dashboard
            switch(resp.data.role){
                case 'ROLE_CUSTOMER' :
                    navigate(`/customer/${response.data.token}`)
                    break;
                case 'ROLE_ADMIN' :
                    navigate(`/admin/${response.data.token}`)
                    break;
                case 'ROLE_SELLER' :
                    console.log('go to seller dashboard')
                    break;
                default:
                    setErrMsg('Something went wrong...')
                    break;
            }
        }
        catch (err) {
            if (err?.response?.data === undefined) {
                setErrMsg("Invalid Credentials")
            }
            else {
                console.log(err?.response?.data)
                setErrMsg(err?.response?.data?.message)
                setErrComment(err?.response?.data?.comments)
            }
        }
    }
    return (
        <div style={{ backgroundColor: '#f1f3f6', minHeight: '100vh' }}>
            <div className="container">
                <div className="row justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                    <div className="col-12 col-sm-10 col-md-9 col-lg-7 col-xl-6">

                        {/* <!-- Flipkart style split login card --> */}
                        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
                            <div className="row g-0">

                                {/* <!-- Left blue panel --> */}
                                <div className="col-md-4 d-flex flex-column justify-content-center text-white p-4"
                                    style={{ backgroundColor: '#2874F0', minHeight: 300 }}>
                                    <h3 className="fw-bold mb-2">Login</h3>
                                    <p className="mb-0" style={{ fontSize: '0.95rem', lineHeight: 1.5 }}>
                                        Get access to your Orders, Wishlist and Recommendations
                                    </p>
                                </div>

                                {/* <!-- Right form panel --> */}
                                <div className="col-md-8 p-4 p-md-5">

                                    {/* <!-- Error / Header --> */}
                                    <div className="mb-3">
                                        <p style={{ color: '#d32f2f', fontSize: '1rem', fontWeight: 600 }}>
                                            {errMsg}
                                        </p>
                                        <p className="text-muted small">
                                            {errComment}
                                        </p>
                                    </div>

                                    {/* <!-- Login Form --> */}
                                    <form onSubmit={(e) => processLogin(e)}>
                                        {/* <!-- Username Field --> */}
                                        <div className="mb-4">
                                            <input type="text" className="form-control border-0 border-bottom rounded-0 px-0"
                                                style={{ backgroundColor: 'transparent', fontSize: '1rem' }}
                                                placeholder="Enter Username" required
                                                onChange={(e) => setUsername(e.target.value)} />
                                        </div>

                                        {/* <!-- Password Field --> */}
                                        <div className="mb-4">
                                            <div className="d-flex justify-content-between align-items-center">
                                                <input type="password" className="form-control border-0 border-bottom rounded-0 px-0 me-3"
                                                    style={{ backgroundColor: 'transparent', fontSize: '1rem' }}
                                                    placeholder="Enter Password" required
                                                    onChange={(e) => setPassword(e.target.value)} />
                                                <a href="#" className="text-decoration-none fw-semibold" style={{ color: '#FB641B', whiteSpace: 'nowrap', fontSize: '0.9rem' }}>Forgot?</a>
                                            </div>
                                        </div>

                                        {/* <!-- Submit Button --> */}
                                        <div className="d-grid mb-4 mt-4">
                                            <button type="submit" className="btn fw-bold text-white"
                                                style={{ backgroundColor: '#FB641B', borderRadius: 2, padding: '0.65rem 0', letterSpacing: 0.4 }}>
                                                SIGN IN
                                            </button>
                                        </div>

                                        {/* <!-- Footer Link --> */}
                                        <div className="text-center mt-4">
                                            <p className="small mb-0" style={{ color: '#878787' }}>
                                                New to QuitQ? <Link to="/customer/signup" className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>Create an account</Link>
                                            </p>
                                        </div>
                                    </form>

                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login
