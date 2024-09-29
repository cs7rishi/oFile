import React, { useEffect, useState } from 'react'
import { URLConstants } from '../constants/URLConstants';
import { File } from './File';

export const Downloads = () => {
  const [files, setFile] = useState([]);

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
      <button type="button" class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800">Add New URL</button>
      </div>
      {/* {files && files.map((file,index) => (
        <File key={index} fileName={file.fileName} fileType={file.fileType} progress={file.progress}/>
      ))} */}
    </>
  )
}
