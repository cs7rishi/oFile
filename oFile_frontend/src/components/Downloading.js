import React, { useEffect, useState } from 'react'
import { URLConstants } from '../constants/URLConstants';
import { File } from './File';

export const Downloading = () => {
  console.log("Downloading component attached");
  const [files, setFile] = useState([]);

  useEffect(() => {
    const eventSource = new EventSource(URLConstants.BASE_URL + URLConstants.FILES_STATUS_ENDPOINT);
    eventSource.onopen = (msg) =>{
      console.log(msg);
    }
    eventSource.onmessage = (event)=>{
      const parsedData = JSON.parse(event.data)
      setFile(parsedData)
      parsedData.map(file =>{
        console.log(`${file.fileName} ${file.progress} ${file.downloadedSize} ${file.fileSize}`)
      })
    }
    eventSource.onerror = (error) => {
      console.error('EventSource failed:', error);
      eventSource.close();
    };
    // const fetchUserFiles = async () => {
    //   console.log("fetchuserFiles is triggered");
    //   try {
    //     let authorization = sessionStorage.getItem('Authorization');
    //     if (authorization === null || authorization === undefined) {
    //       alert('The property is not found in local storage.');
    //     } else {
    //       const response = await fetch(URLConstants.BASE_URL + URLConstants.FILES_STATUS_ENDPOINT, {
    //         method: 'GET',
    //         headers: {
    //           Authorization: `${authorization}`,
    //         },
    //       });

    //       if (response.ok) {
    //         const data = await response.json()
    //         setFile(data);
    //         console.log(data);
    //       }
    //     }
    //   } catch (error) {
    //     console.log(error)
    //   }
    // }
    // fetchUserFiles();
  }, []);

  return (
    <>
      <div>Downloads</div>
      {files && files.map((file,index) => (
        <File key={index} fileName={file.fileName} downloadedSize={file.downloadedSize} progress={file.progress} fileSize={file.fileSize}/>
      ))}
    </>
  )
}
