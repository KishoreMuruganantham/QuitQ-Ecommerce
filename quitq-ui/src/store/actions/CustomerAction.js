import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

export const getCartItems = (customerId)=>async(dispatch)=>{

    try{
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
    catch{
        /* cart not found for this customer -> treat it as an empty cart */
        dispatch({
            type: 'FETCH_CART_CUSTOMER',
            payload : []
        })
    }
}

export const deleteCartItem = (cartItemId) =>async(dispatch)=>{
    try{
        await axios.delete(`${API_SERVER_URL_DEV}/api/cart/item/${cartItemId}` ,
            {
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
            })
        dispatch({
            type: 'DELETE_CART_ITEM',
            payload : cartItemId
        })
        return { ok: true }
    }
    catch(err){
        return { ok: false, message: err?.response?.data?.message || "Something went wrong" }
    }
}

export const updateQuantity = (cartItemId, quantity)=>async(dispatch)=>{
    try{
        const response = await axios.put(`${API_SERVER_URL_DEV}/api/cart/item/${cartItemId}?quantity=${quantity}`, null ,
            {
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
            })
        dispatch({
            type: 'UPDATE_CART_ITEM',
            payload : response.data
        })
        return { ok: true }
    }
    catch(err){
        return { ok: false, message: err?.response?.data?.message || "Something went wrong" }
    }
}
