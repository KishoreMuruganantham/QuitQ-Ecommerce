import { useState } from "react"
import { Link, useNavigate } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config"
import axios from "axios"

function SignUp(){
     const [name, setName] = useState("")
      const [gender, setGender] = useState("")
       const [contactNumber, setContactNumber] = useState("")
    const [email, setEmail] = useState("")
const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")

    const [errMsg, setErrMsg] = useState("")
    const [errComment, setErrComment] = useState("")
    const navigate = useNavigate()

    const signUpApi = API_SERVER_URL_DEV + "/api/customer/signup"
    const processSignup =async (e)=>{
        e.preventDefault()
        let customerDto ={
            'name': name,
            'gender': gender,
            'contactNumber': contactNumber,
            'email': email,
            'username':username,
            'password':password
        }
        try{
        await axios.post(signUpApi, customerDto)
            navigate("/")
        }
        catch(err){
            setErrMsg(err?.response?.data?.message)
            //setErrComment(err?.response?.data?.comments)
        }
    }
    return(
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
                                    <h3 className="fw-bold mb-2">Looks like you're new here!</h3>
                                    <p className="mb-0" style={{ fontSize: '0.95rem', lineHeight: 1.5 }}>
                                        Sign up with your details to get started with QuitQ
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
                                            {/* {errComment} */}
                                        </p>
                                    </div>

                                    {/* <!-- Sign-Up Form --> */}
                                    <form onSubmit={(e) => processSignup(e)}>
                                        <div className="row">
                                            <div className="col-md-6">
                                                {/* <!-- name Field --> */}
                                                <div className="mb-3">
                                                    <label className="form-label small fw-semibold text-secondary">Name</label>
                                                    <input type="text" className="form-control border-0 border-bottom rounded-0 px-0"
                                                        style={{ backgroundColor: 'transparent', fontSize: '1rem' }} required
                                                        onChange={(e) => setName(e.target.value)} />
                                                </div>
                                                {/* <!-- gender Field --> */}
                                                <div className="mb-3">
                                                    <label className="form-label small fw-semibold text-secondary">Gender</label>
                                                    <input type="text" className="form-control border-0 border-bottom rounded-0 px-0"
                                                        style={{ backgroundColor: 'transparent', fontSize: '1rem' }} required
                                                        onChange={(e) => setGender(e.target.value)} />
                                                </div>
                                                {/* <!-- contactNumber Field --> */}
                                                <div className="mb-3">
                                                    <label className="form-label small fw-semibold text-secondary">Contact Number</label>
                                                    <input type="text" className="form-control border-0 border-bottom rounded-0 px-0"
                                                        style={{ backgroundColor: 'transparent', fontSize: '1rem' }} required
                                                        onChange={(e) => setContactNumber(e.target.value)} />
                                                </div>
                                            </div>
                                            <div className="col-md-6">
                                                {/* <!-- email Field --> */}
                                                <div className="mb-3">
                                                    <label className="form-label small fw-semibold text-secondary">Email</label>
                                                    <input type="email" className="form-control border-0 border-bottom rounded-0 px-0"
                                                        style={{ backgroundColor: 'transparent', fontSize: '1rem' }} required
                                                        onChange={(e) => setEmail(e.target.value)} />
                                                </div>

                                                {/* <!-- Username Field --> */}
                                                <div className="mb-3">
                                                    <label className="form-label small fw-semibold text-secondary">Username</label>
                                                    <input type="text" className="form-control border-0 border-bottom rounded-0 px-0"
                                                        style={{ backgroundColor: 'transparent', fontSize: '1rem' }} required
                                                        onChange={(e) => setUsername(e.target.value)} />
                                                </div>

                                                {/* <!-- Password Field --> */}
                                                <div className="mb-3">
                                                    <label className="form-label small fw-semibold text-secondary">Password</label>
                                                    <input type="password" className="form-control border-0 border-bottom rounded-0 px-0"
                                                        style={{ backgroundColor: 'transparent', fontSize: '1rem' }}
                                                        placeholder="••••••••" required
                                                        onChange={(e) => setPassword(e.target.value)} />
                                                </div>
                                            </div>
                                        </div>

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
