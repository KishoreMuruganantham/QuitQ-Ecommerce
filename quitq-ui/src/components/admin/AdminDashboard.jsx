import axios from "axios";
import { useEffect } from "react"
import { useNavigate, useParams } from "react-router"
import { API_SERVER_URL_DEV } from "../../../config";

function AdminDashboard() {

    const navigate = useNavigate()
    let params = useParams();
    const userDetailsUrl = API_SERVER_URL_DEV + '/api/auth/user-details'
    
    useEffect(() => {
        console.log('in use effect....')
        const urlToken = params.token
        const memToken = localStorage.getItem('token')
        if (urlToken !== memToken) {
            navigate("/no-access")
        }
        console.log(`tokens match: ${urlToken === memToken}`)

        const getUserDetails = async() => {
            console.log('in user details Fn')
            try {
                // verify this token by calling user-details API
                // Step 2. Fetch User Details using memToken
                const resp = await axios.get(userDetailsUrl, {
                    headers: { 'Authorization': 'Bearer ' + memToken }
                })
                console.log(resp)
                if(! (resp.data.role === 'ROLE_ADMIN')){
                    navigate("/no-access")
                }
            }
            catch (err) {
                navigate("/no-access")
            }
        }

         getUserDetails()
    }, [params])
    const logout = () => {
        localStorage.clear()
        navigate("/")
    }
    return (
        <div>
            <h1>AdminDashboard</h1>
            <button className="btn btn-secondary" onClick={() => logout()}>Logout</button>
        </div>
    )
}

export default AdminDashboard
