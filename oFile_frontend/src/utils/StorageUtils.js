import { Constants } from "../constants/Constant";

export const getStorage = (key) => {
    return window.sessionStorage.getItem(key)
}
export const clearStorage = () => {
    window.sessionStorage.clear();
}
export const setStorage = (key, value) => {
    window.sessionStorage.setItem(key, value);
}