import { Constants } from "../constants/Constant";

export const getStorage = () => {
    sessionStorage.getItem(Constants.AUTHORIZATION)
}
export const clearStorage = () => {
    window.sessionStorage.clear();
}
export const setStorage = (key, value) => {
    window.sessionStorage.setItem(key, value);
}