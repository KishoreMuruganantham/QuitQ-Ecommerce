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
        <div className="container">
            <div className="row justify-content-center">
                <div className="col-12 col-sm-9 col-md-7 col-lg-5 col-xl-4">

                    {/* <!-- Login Card --> */}
                    <div className="card shadow-sm border-0 rounded-4 p-4 p-sm-5 bg-white">
                        <div className="card-body p-0">

                            {/* <!-- Branding / Header --> */}
                            <div className="text-center mb-4">

                                <h1 className="h4 fw-bold text-dark mb-1">QuitQ</h1>
                                <p className="text-muted small">Please enter your details to sign up.</p>
                            </div>
                            {/* <!-- Error / Header --> */}
                            <div className="text-center mb-4">

                                <p style={{ color: 'red', fontSize: '1.2rem' }}>
                                    {errMsg}
                                </p>
                                <p className="text-muted small">
                                    {/* {errComment} */}
                                </p>
                            </div>

                            {/* <!-- Login Form --> */}
                            <form onSubmit={(e) => processSignup(e)}>
                                {/* <!-- name Field --> */}
                                <div className="mb-3">
                                    <label className="form-label small fw-semibold text-secondary">Name</label>
                                    <input type="text" className="form-control form-control-lg" required
                                        onChange={(e) => setName(e.target.value)} />
                                </div>
                                {/* <!-- gender Field --> */}
                                <div className="mb-3">
                                    <label className="form-label small fw-semibold text-secondary">Gender</label>
                                    <input type="text" className="form-control form-control-lg" required
                                        onChange={(e) => setGender(e.target.value)} />
                                </div>
                                {/* <!-- contactNumber Field --> */}
                                <div className="mb-3">
                                    <label className="form-label small fw-semibold text-secondary">Contact Number</label>
                                    <input type="text" className="form-control form-control-lg" required
                                        onChange={(e) => setContactNumber(e.target.value)} />
                                </div>
                                {/* <!-- email Field --> */}
                                <div className="mb-3">
                                    <label className="form-label small fw-semibold text-secondary">Email</label>
                                    <input type="email" className="form-control form-control-lg" required
                                        onChange={(e) => setEmail(e.target.value)} />
                                </div>

                                {/* <!-- Username Field --> */}
                                <div className="mb-3">
                                    <label className="form-label small fw-semibold text-secondary">Username</label>
                                    <input type="text" className="form-control form-control-lg" required
                                        onChange={(e) => setUsername(e.target.value)} />
                                </div>

                                {/* <!-- Password Field --> */}
                                <div className="mb-3">
                                    <div className="d-flex justify-content-between align-items-center">
                                        <label className="form-label small fw-semibold text-secondary">Password</label>
                                     </div>
                                    <input type="password" className="form-control form-control-lg" placeholder="••••••••" required
                                        onChange={(e) => setPassword(e.target.value)} />
                                </div>

                                {/* <!-- Submit Button --> */}
                                <div className="d-grid mb-3">
                                    <button type="submit" className="btn btn-primary btn-lg fw-semibold"  >Sign up</button>
                                </div>

                                {/* <!-- Footer Link --> */}
                                <div className="text-center">
                                    <p className="small text-muted mb-0">Have an account? <Link to="/" className="text-primary text-decoration-none fw-semibold">Login</Link></p>
                                </div>
                            </form>

                        </div>
                    </div>

                </div>
            </div>
        </div>
    )
}

export default SignUp
