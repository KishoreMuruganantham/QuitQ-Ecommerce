import { useEffect, useState } from "react"
import { Link, useNavigate } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"
import ProfileDropdown from "./ProfileDropdown"

function Navbar() {
    const navigate = useNavigate()
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('role')

    const [keyword, setKeyword] = useState("")
    const [suggestions, setSuggestions] = useState([])
    const [showSuggest, setShowSuggest] = useState(false)

    const rootPath = role === 'ROLE_SELLER' ? `/seller/${token}`
        : role === 'ROLE_ADMIN' ? `/admin/${token}`
            : `/customer/${token}`

    const onKeywordChange = (e) => {
        const value = e.target.value
        setKeyword(value)
        if (value.trim().length < 2) {
            setSuggestions([])
            setShowSuggest(false)
        }
    }

    useEffect(() => {
        if (role !== 'ROLE_CUSTOMER' || keyword.trim().length < 2) return
        const timer = setTimeout(async () => {
            try {
                const resp = await axios.get(
                    `${API_SERVER_URL_DEV}/api/product/search?keyword=${encodeURIComponent(keyword.trim())}&size=5`
                )
                setSuggestions(resp.data)
                setShowSuggest(true)
            } catch {
                setSuggestions([])
            }
        }, 250)
        return () => clearTimeout(timer)
    }, [keyword, role])

    const submitSearch = (e) => {
        e.preventDefault()
        setShowSuggest(false)
        if (keyword.trim()) {
            navigate(`/customer/${token}?q=${encodeURIComponent(keyword.trim())}`)
        }
    }

    const pickSuggestion = (productId) => {
        navigate(`/customer/${token}/product/${productId}`)
        setKeyword("")
        setSuggestions([])
        setShowSuggest(false)
    }

    return (
        <nav className="navbar navbar-expand-lg fk-nav-shell" style={{ backgroundColor: '#2874F0', padding: '8px 0' }}>
            <div className="d-flex align-items-center gap-2 gap-md-3 flex-wrap px-3 px-md-4 mx-auto"
                style={{ maxWidth: 1280, width: '100%' }}>

                {/* <!-- Logo --> */}
                <Link to={rootPath} className="d-flex align-items-center text-decoration-none me-1">
                    <img src="/logo-transparent.png" alt="QuitQ" style={{ height: 38 }} />
                </Link>

                {/* <!-- Search with live suggestions (customer only) --> */}
                {role === 'ROLE_CUSTOMER' && (
                    <div className="fk-search-wrap">
                        <form className="fk-search" onSubmit={(e) => submitSearch(e)}>
                            <input type="text" placeholder="Search for products, brands and more"
                                value={keyword}
                                onChange={(e) => onKeywordChange(e)}
                                onFocus={() => { if (suggestions.length > 0) setShowSuggest(true) }} />
                            <button type="submit" aria-label="Search"><i className="bi bi-search"></i></button>
                        </form>
                        {showSuggest && suggestions.length > 0 && (
                            <div className="fk-suggest">
                                {suggestions.map((p) => (
                                    <div key={p.id} className="fk-suggest-item"
                                        onMouseDown={(e) => e.preventDefault()}
                                        onClick={() => pickSuggestion(p.id)}>
                                        {p.imageUrl && <img src={p.imageUrl} alt="" />}
                                        <span className="text-truncate">{p.name}</span>
                                        <span className="ms-auto fw-semibold text-nowrap">Rs. {Number(p.price).toLocaleString('en-IN')}</span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* <!-- Spacer for roles without search --> */}
                {role !== 'ROLE_CUSTOMER' && <div className="flex-grow-1"></div>}

                {/* <!-- Same line: Home, Profile, Cart (Flipkart style) --> */}
                <div className="d-flex align-items-center ms-auto gap-1 flex-wrap">
                    <Link className="fk-nav-link" to={rootPath}>
                        <i className="bi bi-house-door me-1"></i>Home
                    </Link>
                    <ProfileDropdown />
                    {role === 'ROLE_CUSTOMER' && (
                        <Link className="fk-nav-link" to={`/customer/${token}/cart`}>
                            <i className="bi bi-cart3 me-1"></i>Cart
                        </Link>
                    )}
                </div>
            </div>
        </nav>
    )
}

export default Navbar
