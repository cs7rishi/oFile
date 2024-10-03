import React, { useEffect, useState } from 'react'
import { URLConstants } from '../constants/URLConstants';
import { File } from './File';
import { Description, Dialog, DialogPanel, DialogTitle } from '@headlessui/react'
import toast from 'react-hot-toast';
import { getStorage } from '../utils';
import { Constants } from '../constants';

export const Downloads = () => {
  const [files, setFile] = useState([]);
  let [isOpen, setIsOpen] = useState(false)

  const handleFileAdd = async e => {
    e.preventDefault();
    const fileName = e.target.fileName.value;
    const fileUrl = e.target.fileUrl.value;

    console.log(fileName + "  " + fileUrl);

    try {
      let authorization = getStorage(Constants.AUTHORIZATION);
      if (authorization === null || authorization === undefined) {
        //Todo abstract the same to a hook
        alert('The property is not found in local storage.');
      } else {
        const response = await fetch(URLConstants.BASE_URL + URLConstants.FILE_ADD_ENDPOINT, {
          method: 'POST',
          headers: {
            "Authorization": `${authorization}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            "fileName": fileName,
            "fileUrl": fileUrl
          })
        });

        if (response.ok) {
          setIsOpen(false);
          toast.success("File added successfully");
        }else{
          toast.error("Something went wrong")
        }
      }
    } catch (error) {
      console.log(error)
    }
  }

  useEffect(() => {
    console.log("component has loaded");
    const fetchUserFiles = async () => {
      console.log("fetchuserFiles is triggered");
      try {
        let authorization = sessionStorage.getItem('Authorization');
        if (authorization === null || authorization === undefined) {
          alert('The property is not found in local storage.');
        } else {
          const response = await fetch(URLConstants.BASE_URL + URLConstants.FILES_LIST_ENDPOINT, {
            method: 'GET',
            headers: {
              Authorization: `${authorization}`,
            },
          });

          if (response.ok) {
            const data = await response.json()
            setFile(data);
            console.log(data);
          }
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchUserFiles();
  }, []);

  return (
    <>
      <div className="action-container flex justify-end mb-4">
        <button onClick={() => setIsOpen(true)} className="block text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">Add File</button>
        <Dialog open={isOpen} onClose={() => setIsOpen(false)} className="relative z-50">
          <div className="fixed inset-0 flex flex-col w-screen items-center justify-center p-4">
            <div className="dialog-head flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                Download New File
              </h3>
              <button onClick={() => setIsOpen(false)} type="button" className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                  <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                </svg>
                <span className="sr-only">Close modal</span>
              </button>
            </div>
            <form className="p-4 md:p-5" onSubmit={handleFileAdd}>
              <div className="grid gap-4 mb-4 grid-cols-2">
                <div className="col-span-2">
                  <label htmlFor="name" className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">File Name</label>
                  <input type="text" name="fileName" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500" placeholder="Type file name" required="" />
                </div>
                <div className="col-span-2">
                  <label htmlFor="name" className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">File Url</label>
                  <input type="text" name="fileUrl" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500" placeholder="Type file url" required="" />
                </div>
              </div>
              <button type="submit" className="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
                <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                Download
              </button>
            </form>
            {/* <DialogPanel className="max-w-lg space-y-4 border bg-white p-12">
              <DialogTitle className="font-bold">Deactivate account</DialogTitle>
              <Description>This will permanently deactivate your account</Description>
              <p>Are you sure you want to deactivate your account? All of your data will be permanently removed.</p>
              <div className="flex gap-4">
                <button onClick={() => setIsOpen(false)}>Cancel</button>
                <button onClick={() => setIsOpen(false)}>Deactivate</button>
              </div>
            </DialogPanel> */}
          </div>
        </Dialog>


      </div>

      {/* {files && files.map((file,index) => (
        <File key={index} fileName={file.fileName} downloadedSize={file.downloadedSize} progress={file.progress} fileSize={file.fileSize}/>
      ))} */}
    </>
  )
}
