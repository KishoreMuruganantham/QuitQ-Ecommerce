import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

export const getCartItems = (customerId)=>async(dispatch)=>{

    const response = await axios.get(`${API_SERVER_URL_DEV}/api/cart/${customerId}`, 
        {
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
        }
    )

    dispatch({
        type: 'FETCH_CART_CUSTOMER',
        payload : response.data.items
    })
}

export const addToCart = (customerId, productId, quantity)=>async(dispatch)=>{
    
    const response = await axios.post(`${API_SERVER_URL_DEV}/api/cart/add/${customerId}/${productId}?quantity=${quantity}`, null ,
        {
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
        })
    dispatch({
        type: 'ADD_CART_ITEM',
        payload : response.data
    })    
}

export const deleteCartItem = (cartItemId) =>async(dispatch)=>{
    const response = await axios.delete(`${API_SERVER_URL_DEV}/api/cart/item/${cartItemId}` ,
        {
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
        })
    dispatch({
        type: 'DELETE_CART_ITEM',
        payload : cartItemId
    })        

}
