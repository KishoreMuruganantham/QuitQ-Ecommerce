import { useState } from "react"
import { Link, useNavigate } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config"
import axios from "axios"

function SignUp() {
    // Role selection: 'CUSTOMER' (default) or 'SELLER'
    const [role, setRole] = useState("CUSTOMER")

    // Customer fields
    const [name, setName] = useState("")
    const [gender, setGender] = useState("")

    // Seller fields
    const [businessName, setBusinessName] = useState("")
    const [city, setCity] = useState("")
    const [address, setAddress] = useState("")

    // Common fields
    const [contactNumber, setContactNumber] = useState("")
    const [email, setEmail] = useState("")
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")

    const [errMsg, setErrMsg] = useState("")

    const navigate = useNavigate()

    const customerSignUpApi = API_SERVER_URL_DEV + "/api/customer/signup"
    const sellerSignUpApi = API_SERVER_URL_DEV + "/api/seller/signup"

    const processSignup = async (e) => {
        e.preventDefault()
        // Build the dto based on the selected role
        let signUpDto
        let signUpApi
        if (role === 'CUSTOMER') {
            signUpDto = {
                'name': name,
                'gender': gender,
                'contactNumber': contactNumber,
                'email': email,
                'username': username,
                'password': password
            }
            signUpApi = customerSignUpApi
        }
        else {
            signUpDto = {
                'businessName': businessName,
                'contactNumber': contactNumber,
                'city': city,
                'address': address,
                'username': username,
                'email': email,
                'password': password
            }
            signUpApi = sellerSignUpApi
        }
        try {
            await axios.post(signUpApi, signUpDto)
            navigate("/")
        }
        catch (err) {
            setErrMsg(err?.response?.data?.message || "Something went wrong")
        }
    }

    // Tiny live strength scorer: length >= 8, upper+lower letters, a digit, a special char
    const passwordStrength = (pwd) => {
        let score = 0
        if (pwd.length >= 8) score++
        if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) score++
        if (/\d/.test(pwd)) score++
        if (/[^A-Za-z0-9]/.test(pwd)) score++
        return score
    }

    const strengthScore = passwordStrength(password)
    const strengthLabel = strengthScore <= 1 ? "Weak" : strengthScore <= 3 ? "Fair" : "Strong"
    const strengthColor = strengthScore <= 1 ? "#d32f2f" : strengthScore <= 3 ? "#FB641B" : "#388E3C"

    return (
        <div style={{ backgroundColor: '#f1f3f6', minHeight: '100vh' }}>
            <div className="container">
                <div className="row justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
                    <div className="col-12 col-sm-11 col-md-10 col-lg-8 col-xl-7">

                        {/* <!-- Flipkart style split signup card --> */}
                        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
                            <div className="row g-0">

                                {/* <!-- Left blue panel --> */}
                                <div className="col-md-4 d-flex flex-column justify-content-center text-white p-4"
                                    style={{ backgroundColor: '#2874F0', minHeight: 300 }}>
                                    <img src="/logo-transparent.png" alt="QuitQ" style={{ height: 42 }} />
                                    <h3 className="fw-bold mb-2 mt-3">Looks like you're new here!</h3>
                                    <p className="mb-0" style={{ fontSize: '0.95rem', lineHeight: 1.5 }}>
                                        Sign up with your details to get started with QuitQ
                                    </p>
                                </div>

                                {/* <!-- Right form panel --> */}
                                <div className="col-md-8 p-4 p-md-5">

                                    {/* <!-- Role Selection: Customer / Seller --> */}
                                    <div className="d-flex gap-2 mb-4">
                                        <button type="button" className="btn fw-semibold"
                                            style={role === 'CUSTOMER'
                                                ? { backgroundColor: '#2874F0', color: '#ffffff', border: '1px solid #2874F0', borderRadius: 2 }
                                                : { backgroundColor: '#ffffff', color: '#2874F0', border: '1px solid #2874F0', borderRadius: 2 }}
                                            onClick={() => setRole('CUSTOMER')}>
                                            Customer
                                        </button>
                                        <button type="button" className="btn fw-semibold"
                                            style={role === 'SELLER'
                                                ? { backgroundColor: '#2874F0', color: '#ffffff', border: '1px solid #2874F0', borderRadius: 2 }
                                                : { backgroundColor: '#ffffff', color: '#2874F0', border: '1px solid #2874F0', borderRadius: 2 }}
                                            onClick={() => setRole('SELLER')}>
                                            Seller
                                        </button>
                                    </div>

                                    {/* <!-- Error / Header --> */}
                                    <div className="mb-3">
                                        <p style={{ color: '#d32f2f', fontSize: '1rem', fontWeight: 600 }}>
                                            {errMsg}
                                        </p>
                                    </div>

                                    {/* <!-- Sign-Up Form --> */}
                                    <form onSubmit={(e) => processSignup(e)}>

                                        {role === 'CUSTOMER' ? (
                                            <div className="row">
                                                <div className="col-md-6">
                                                    {/* <!-- name Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Name</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setName(e.target.value)} />
                                                    </div>
                                                    {/* <!-- gender Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Gender</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                            onChange={(e) => setGender(e.target.value)} />
                                                    </div>
                                                    {/* <!-- contactNumber Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Contact Number</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setContactNumber(e.target.value)} />
                                                    </div>
                                                </div>
                                                <div className="col-md-6">
                                                    {/* <!-- email Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Email</label>
                                                        <input type="email" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setEmail(e.target.value)} />
                                                    </div>

                                                    {/* <!-- Username Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Username</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setUsername(e.target.value)} />
                                                    </div>

                                                    {/* <!-- Password Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Password</label>
                                                        <input type="password" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                            placeholder="Minimum 8 characters" minLength={8} required
                                                            onChange={(e) => setPassword(e.target.value)} />
                                                        {password && (
                                                            <div className="d-flex align-items-center gap-2 mt-1">
                                                                <div style={{ flex: 1, height: 4, backgroundColor: '#e0e0e0', borderRadius: 2 }}>
                                                                    <div style={{ width: `${(strengthScore / 4) * 100}%`, height: 4, backgroundColor: strengthColor, borderRadius: 2 }}></div>
                                                                </div>
                                                                <small style={{ color: strengthColor, fontSize: 12, fontWeight: 600 }}>{strengthLabel}</small>
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        ) : (
                                            <div className="row">
                                                <div className="col-md-6">
                                                    {/* <!-- businessName Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Business Name</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setBusinessName(e.target.value)} />
                                                    </div>
                                                    {/* <!-- contactNumber Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Contact Number</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setContactNumber(e.target.value)} />
                                                    </div>
                                                    {/* <!-- city Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">City</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                            onChange={(e) => setCity(e.target.value)} />
                                                    </div>
                                                    {/* <!-- address Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Address</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                            onChange={(e) => setAddress(e.target.value)} />
                                                    </div>
                                                </div>
                                                <div className="col-md-6">
                                                    {/* <!-- email Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Email</label>
                                                        <input type="email" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setEmail(e.target.value)} />
                                                    </div>

                                                    {/* <!-- Username Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Username</label>
                                                        <input type="text" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input" required
                                                            onChange={(e) => setUsername(e.target.value)} />
                                                    </div>

                                                    {/* <!-- Password Field --> */}
                                                    <div className="mb-3">
                                                        <label className="form-label small fw-semibold text-secondary">Password</label>
                                                        <input type="password" className="form-control border-0 border-bottom rounded-0 px-0 fk-auth-input"
                                                            placeholder="Minimum 8 characters" minLength={8} required
                                                            onChange={(e) => setPassword(e.target.value)} />
                                                        {password && (
                                                            <div className="d-flex align-items-center gap-2 mt-1">
                                                                <div style={{ flex: 1, height: 4, backgroundColor: '#e0e0e0', borderRadius: 2 }}>
                                                                    <div style={{ width: `${(strengthScore / 4) * 100}%`, height: 4, backgroundColor: strengthColor, borderRadius: 2 }}></div>
                                                                </div>
                                                                <small style={{ color: strengthColor, fontSize: 12, fontWeight: 600 }}>{strengthLabel}</small>
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        )}

                                        {/* <!-- Submit Button --> */}
                                        <div className="d-grid mt-3 mb-4">
                                            <button type="submit" className="btn fw-bold text-white"
                                                style={{ backgroundColor: '#FB641B', borderRadius: 2, padding: '0.65rem 0', letterSpacing: 0.4 }}>
                                                SIGN UP
                                            </button>
                                        </div>

                                        {/* <!-- Footer Link --> */}
                                        <div className="text-center">
                                            <p className="small mb-0" style={{ color: '#878787' }}>
                                                Have an account? <Link to="/" className="fw-semibold text-decoration-none" style={{ color: '#2874F0' }}>Login</Link>
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

export default SignUp
