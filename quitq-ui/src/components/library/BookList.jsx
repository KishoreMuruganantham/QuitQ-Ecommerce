import { useEffect, useState } from "react"
import axios from "axios"
import "./BookList.css"

function BookList() {
    const [books, setBooks] = useState([])
    const [minYear, setMinYear] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")

    useEffect(() => {
        let active = true

        const loadBooks = async () => {
            setLoading(true)
            setError("")

            try {
                let response

                if (minYear.trim() === "") {
                    response = await axios.get("http://localhost:8080/api/books")
                } else {
                    response = await axios.get("http://localhost:8080/api/books/search", {
                        params: { minYear: minYear }
                    })
                }

                if (active) {
                    setBooks(response.data)
                }
            } catch (err) {
                if (active) {
                    setError("Unable to load books. Please check whether the backend is running.")
                    setBooks([])
                }
            } finally {
                if (active) {
                    setLoading(false)
                }
            }
        }

        loadBooks()

        return () => {
            active = false
        }
    }, [minYear])

    return (
        <div className="library-page">
            <div className="library-container">
                <h2>Library Books</h2>
                <p>Browse books and filter them by publication year.</p>

                <div className="library-search">
                    <label htmlFor="minYear">Minimum Published Year</label>
                    <input
                        id="minYear"
                        type="number"
                        min="0"
                        placeholder="Enter year, e.g. 2015"
                        value={minYear}
                        onChange={(event) => setMinYear(event.target.value)}
                    />
                </div>

                {loading && <p className="library-message">Loading books...</p>}
                {error && <p className="library-error">{error}</p>}

                {!loading && !error && (
                    <div className="library-table-wrap">
                        <table className="library-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Title</th>
                                    <th>Genre</th>
                                    <th>Published Year</th>
                                    <th>Price</th>
                                    <th>Author</th>
                                </tr>
                            </thead>
                            <tbody>
                                {books.length > 0 ? books.map((book) => (
                                    <tr key={book.id}>
                                        <td>{book.id}</td>
                                        <td>{book.title}</td>
                                        <td>{book.genre}</td>
                                        <td>{book.publishedYear}</td>
                                        <td>{book.price}</td>
                                        <td>{book.authorName}</td>
                                    </tr>
                                )) : (
                                    <tr>
                                        <td colSpan="6" className="library-empty">
                                            No books found.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}

export default BookList
