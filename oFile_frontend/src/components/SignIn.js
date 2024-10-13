import { useNavigate } from "react-router-dom";
import { URLConstants, Constants } from "../constants";
import { setStorage, triggerNotification } from "../utils";
import toast from "react-hot-toast"

export const SignIn = () => {
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    const email = e.target.email.value;
    const password = e.target.password.value
    handleSignIn(email, password);
  }

  const handleSignIn = async (email, password) => {
    try {
      const encodedCredentials = btoa(`${email}:${password}`);
      const response = await fetch(URLConstants.BASE_URL + URLConstants.LOGIN_ENDPOINT, {
        method: 'GET',
        headers: {
          Authorization: `Basic ${encodedCredentials}`,
        },
      });
      if (response.status === 200) {
        const data = await response.json();
        setStorage(Constants.AUTHORIZATION, response.headers.get(Constants.AUTHORIZATION))
        triggerNotification(data.response)
        navigate("/dashboard/downloads")
      }
    } catch (error) {
      toast.error("Something went Wrong!");
    }
  }

  return (
    <div className="login-container flex items-center w-full h-full bg-raisin-black">
      <form className="w-1/3 mx-auto max-h-max" onSubmit={handleSubmit}>
        <div className="mb-5">
          <label htmlFor="email" className="block mb-2 text-sm font-medium text-white">Your email</label>
          <input name="email" type="email" id="email" className="bg-alice-blue border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5" placeholder="Email" required />
        </div>
        <div className="mb-5">
          <label htmlFor="password" className="block mb-2 text-sm font-medium text-white">Your password</label>
          <input name="password" type="password" id="password" className="bg-alice-blue border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5" placeholder="Password" required />
        </div>
        {/* <div className="flex items-start mb-5">
          <div className="flex items-center h-5">
            <input id="remember" type="checkbox" value="" className="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-blue-300 dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-blue-600 dark:ring-offset-gray-800 dark:focus:ring-offset-gray-800" required />
          </div>
          <label htmlFor="remember" className="ms-2 text-sm font-medium text-gray-900 dark:text-gray-300">Remember me</label>
        </div> */}
        <button type="submit" className="text-white bg-picton-blue hover:bg-picton-blue-800 focus:ring-4 focus:outline-none focus:ring-picton-blue font-medium rounded-lg text-sm w-full sm:w-auto px-5 py-2.5 text-center">Submit</button>
      </form>
    </div>
  )
}
