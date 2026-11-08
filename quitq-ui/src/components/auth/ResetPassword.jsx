import { useState } from "react"
import { Link, useNavigate, useSearchParams } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config"
import axios from "axios"

function ResetPassword() {
    const [newPassword, setNewPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [errMsg, setErrMsg] = useState("")
    const [showNewLink, setShowNewLink] = useState(false)
    const [resetDone, setResetDone] = useState(false)

    const apiUrl = API_SERVER_URL_DEV + '/api/auth/reset-password'

    const navigate = useNavigate()
    const [searchParams] = useSearchParams()
    const token = searchParams.get('token')

    const processResetPassword = async (e) => {
        e.preventDefault()
        setErrMsg("")
        setShowNewLink(false)

        // Client-side check — do not call the API on mismatch
        if (newPassword !== confirmPassword) {
            setErrMsg("Passwords do not match")
            return
        }
        try {
            // {"message": "Password reset successfully"}
            await axios.post(apiUrl, { token, newPassword: newPassword })
            setErrMsg("")
            setResetDone(true)
        }
        catch (err) {
            if (err.response) {
                // Expired / already used token or validation errors from backend
                setErrMsg(err?.response?.data?.message || "Something went wrong...")
                setShowNewLink(true)
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

                        {/* <!-- Flipkart style split reset password card --> */}
                        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
                            <div className="row g-0">

                                {/* <!-- Left blue panel --> */}
                                <div className="col-md-4 d-flex flex-column justify-content-center text-white p-4"
                                    style={{ backgroundColor: '#2874F0', minHeight: 300 }}>
                                    <img src="/logo-transparent.png" alt="QuitQ" style={{ height: 42 }} />
                                    <h3 className="fw-bold mb-2 mt-3">Reset Password</h3>
                                    <p className="mb-0" style={{ fontSize: '0.95rem', lineHeight: 1.5 }}>
                                        Choose a new password for your QuitQ account
                                    </p>
                                </div>

                                {/* <!-- Right form panel --> */}
                                <div className="col-md-8 p-4 p-md-5">

                                    {!token ? (

                                        // Missing / invalid token
                                        <div className="text-center py-3">
                                            <p className="fk-error mb-4">Reset link is missing or invalid.</p>
                                            <Link to="/forgot-password" className="fk-btn-blue text-decoration-none text-center d-block">
                                                REQUEST A NEW LINK
                                            </Link>
                                        </div>

                                    ) : resetDone ? (

                                        // Success state (same card)
                                        <div className="text-center py-3">
                                            <i className="bi bi-check-circle-fill text-success" style={{ fontSize: 56 }}></i>
                                            <h5 className="fw-bold mt-3 mb-2">Password reset successfully</h5>
                                            <p className="fk-muted mb-4">You can now log in with your new password.</p>
                                            <button type="button" className="fk-btn-primary"
                                                onClick={() => navigate("/")}>
                                                LOGIN NOW
                                            </button>
                                        </div>

                                    ) : (

                                        <>
                                            {/* <!-- Error --> */}
                                            <div className="mb-3">
                                                <p className="fk-error">{errMsg}</p>
                                                {showNewLink && (
                                                    <Link to="/forgot-password" className="fw-semibold text-decoration-none" style={{ color: '#2874F0', fontSize: '0.85rem' }}>
                                                        REQUEST A NEW LINK
                                                    </Link>
                                                )}
                                            </div>

                                            {/* <!-- Reset Password Form --> */}
                                            <form onSubmit={(e) => processResetPassword(e)}>
                                                {/* <!-- New Password Field --> */}
                                                <div className="mb-4">
                                                    <label className="fk-label" htmlFor="newPassword">NEW PASSWORD</label>
                                                    <input type="password" id="newPassword" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                        placeholder="Minimum 8 characters" minLength={8} required
                                                        onChange={(e) => setNewPassword(e.target.value)} />
                                                </div>

                                                {/* <!-- Confirm Password Field --> */}
                                                <div className="mb-4">
                                                    <label className="fk-label" htmlFor="confirmPassword">CONFIRM PASSWORD</label>
                                                    <input type="password" id="confirmPassword" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                        placeholder="Re-enter password" required
                                                        onChange={(e) => setConfirmPassword(e.target.value)} />
                                                </div>

                                                {/* <!-- Submit Button --> */}
                                                <div className="d-grid mb-4 mt-4">
                                                    <button type="submit" className="fk-btn-primary">
                                                        RESET PASSWORD
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

export default ResetPassword
