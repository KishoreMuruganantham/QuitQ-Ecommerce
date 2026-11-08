import { useState } from "react"
import { Link, useNavigate } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config"
import axios from "axios"

function ForgotPassword() {
    const [identifier, setIdentifier] = useState("")
    const [errMsg, setErrMsg] = useState("")
    const [sent, setSent] = useState(false)
    const [serverMsg, setServerMsg] = useState("")
    const [resetToken, setResetToken] = useState(null)

    const apiUrl = API_SERVER_URL_DEV + '/api/auth/forgot-password'

    const navigate = useNavigate()

    const processForgotPassword = async (e) => {
        e.preventDefault()
        setErrMsg("")
        try {
            // Always HTTP 200 — resetToken is non-null only when the account exists
            const response = await axios.post(apiUrl, { username: identifier })
            setServerMsg(response.data?.message || "If the account exists, a password reset link has been generated")
            setResetToken(response.data?.resetToken || null)
            setSent(true)
        }
        catch (err) {
            if (err.response) {
                setErrMsg(err?.response?.data?.message || "Something went wrong...")
            }
            else {
                // No response at all -> network / CORS failure
                setErrMsg("Unable to reach server. Please try again.")
            }
        }
    }
    return (
        <div style={{ backgroundColor: '#f1f3f6', minHeight: '100vh' }}>
            <div className="container">
                <div className="row justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                    <div className="col-12 col-sm-10 col-md-9 col-lg-7 col-xl-6">

                        {/* <!-- Flipkart style split forgot password card --> */}
                        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
                            <div className="row g-0">

                                {/* <!-- Left blue panel --> */}
                                <div className="col-md-4 d-flex flex-column justify-content-center text-white p-4"
                                    style={{ backgroundColor: '#2874F0', minHeight: 300 }}>
                                    <img src="/logo-transparent.png" alt="QuitQ" style={{ height: 42 }} />
                                    <h3 className="fw-bold mb-2 mt-3">Forgot Password?</h3>
                                    <p className="mb-0" style={{ fontSize: '0.95rem', lineHeight: 1.5 }}>
                                        Enter your username or registered email and we'll send you a password reset link
                                    </p>
                                </div>

                                {/* <!-- Right form panel --> */}
                                <div className="col-md-8 p-4 p-md-5">

                                    {!sent ? (
                                        <>

                                            {/* <!-- Error --> */}
                                            <div className="mb-3">
                                                <p className="fk-error">{errMsg}</p>
                                            </div>

                                            {/* <!-- Forgot Password Form --> */}
                                            <form onSubmit={(e) => processForgotPassword(e)}>
                                                {/* <!-- Username / Email Field --> */}
                                                <div className="mb-4">
                                                    <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                        placeholder="Enter username or email" required
                                                        onChange={(e) => setIdentifier(e.target.value)} />
                                                </div>

                                                {/* <!-- Submit Button --> */}
                                                <div className="d-grid mb-4 mt-4">
                                                    <button type="submit" className="fk-btn-primary">
                                                        SEND RESET LINK
                                                    </button>
                                                </div>

                                                {/* <!-- Footer Link --> */}
                                                <div className="text-center mt-4">
                                                    <p className="small mb-0" style={{ color: '#878787' }}>
                                                        <Link to="/" className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>Back to Login</Link>
                                                    </p>
                                                </div>
                                            </form>

                                        </>
                                    ) : (

                                        // Success state (same card)
                                        <div className="text-center py-3">
                                            <i className="bi bi-envelope-check-fill text-success" style={{ fontSize: 56 }}></i>
                                            <h5 className="fw-bold mt-3 mb-2">Check your account</h5>
                                            <p className="fk-muted mb-4">{serverMsg}</p>

                                            {resetToken && (
                                                <div className="mb-3">
                                                    <button type="button" className="fk-btn-blue"
                                                        onClick={() => navigate(`/reset-password?token=${resetToken}`)}>
                                                        OPEN RESET LINK
                                                    </button>
                                                    <p className="fk-muted small mt-2 mb-0">
                                                        Demo note: no email service is configured, so the reset link is shown here.
                                                    </p>
                                                </div>
                                            )}

                                            <div className="mt-4">
                                                <Link to="/" className="fw-semibold text-decoration-none" style={{ color: '#2874F0', fontSize: '0.85rem' }}>Back to Login</Link>
                                            </div>
                                        </div>
                                    )}

                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    )
}

export default ForgotPassword
