const initialState={
    cartItems: []
}
const CustomerReducer =(state = initialState,action)=>{
    switch(action.type){
        case 'FETCH_CART_CUSTOMER':
            return {
                ...state, 
                cartItems : action.payload
            }
        case 'ADD_CART_ITEM' :
            return{
                ...state, 
                cartItems : [...state.cartItems, action.payload]
            }
        case 'DELETE_CART_ITEM' :
            return{
                ...state, 
                cartItems : [...state.cartItems.filter(c=> c.cartItemId !== action.payload)]
            } 
        case 'UPDATE_CART_ITEM' :
            return{
                ...state, 
                cartItems : state.cartItems.map(c=> c.cartItemId === action.payload.cartItemId ? action.payload : c)
            } 
              
        default: 
            return state
    }

}

export default CustomerReducer
