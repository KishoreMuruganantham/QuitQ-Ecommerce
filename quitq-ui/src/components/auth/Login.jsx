import { useEffect, useState } from "react"
import { Link, useLocation, useNavigate } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config"
import axios from "axios"

function Login() {
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [errMsg, setErrMsg] = useState("")
    const [errComment, setErrComment] = useState("")

    const apiUrl = API_SERVER_URL_DEV + '/api/auth/login'
    const userDetailsUrl = API_SERVER_URL_DEV + '/api/auth/user-details'

    const navigate = useNavigate()
    const location = useLocation()

    // Auto redirect already logged-in users to their dashboard (only from the login page)
    useEffect(() => {
        const token = localStorage.getItem('token')
        const role = localStorage.getItem('role')
        if (location.pathname === '/' && token && role) {
            switch (role) {
                case 'ROLE_CUSTOMER':
                    navigate(`/customer/${token}`)
                    break;
                case 'ROLE_SELLER':
                    navigate(`/seller/${token}`)
                    break;
                case 'ROLE_ADMIN':
                    navigate(`/admin/${token}`)
                    break;
                default:
                    break;
            }
        }
    }, [location.pathname, navigate])

    const processLogin = async (e) => {
        e.preventDefault()
        // Step 1. Fetch the jwt token
        const token = 'Basic ' + window.btoa(username + ':' + password)
        try {
            const response = await axios.get(apiUrl, {
                headers: { 'Authorization': token }
            })

            localStorage.setItem('token', response.data.token)
            localStorage.setItem('username', username)

            // Step 2. Fetch User Details using jwtToken
            const resp = await axios.get(userDetailsUrl, {
                headers: { 'Authorization': 'Bearer ' + response.data.token }
            })
            localStorage.setItem('role', resp.data.role)
            localStorage.setItem('id', resp.data.id)
            // Based on the role of this user load appropriate dashboard
            switch (resp.data.role) {
                case 'ROLE_CUSTOMER':
                    navigate(`/customer/${response.data.token}`)
                    break;
                case 'ROLE_ADMIN':
                    navigate(`/admin/${response.data.token}`)
                    break;
                case 'ROLE_SELLER':
                    navigate(`/seller/${response.data.token}`)
                    break;
                default:
                    setErrMsg('Something went wrong...')
                    break;
            }
        }
        catch (err) {
            if (err.response) {
                setErrMsg(err?.response?.data?.message || "Invalid Credentials")
                setErrComment(err?.response?.data?.comments)
            }
            else {
                // No response at all -> network / CORS failure, not bad credentials
                setErrMsg("Unable to reach server. Please try again.")
                setErrComment("")
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
                                    <img src="/logo-transparent.png" alt="QuitQ" style={{ height: 42 }} />
                                    <h3 className="fw-bold mb-2 mt-3">Login</h3>
                                    <p className="mb-0" style={{ fontSize: '0.95rem', lineHeight: 1.5 }}>
                                        Get access to your Orders and Recommendations
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
                                            <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                placeholder="Enter Username" required
                                                onChange={(e) => setUsername(e.target.value)} />
                                        </div>

                                        {/* <!-- Password Field --> */}
                                        <div className="mb-4">
                                            <input type="password" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                placeholder="Enter Password" required
                                                onChange={(e) => setPassword(e.target.value)} />
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
