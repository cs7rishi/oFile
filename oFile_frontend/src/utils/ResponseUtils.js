import toast from "react-hot-toast"

export const triggerNotification = (response) => {
    return response.status === 200 ? toast.success(response.msg) : toast.error(response.msg);
}