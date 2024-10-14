import React, { useCallback, useEffect, useState } from 'react'
import { URLConstants } from '../constants/URLConstants';
import { File } from './File';
import { Description, Dialog, DialogPanel, DialogTitle } from '@headlessui/react'
import toast from 'react-hot-toast';
import { getStorage } from '../utils';
import { Constants } from '../constants';

export const Downloads = () => {
  const [files, setFiles] = useState([]);
  const [isStreaming, setIsStreaming] = useState(false);
  const [downloadingFiles, setDownloadingFiles] = useState([]);
  let [isOpen, setIsOpen] = useState(false)

  const handleFileDelete = async (fileId) => {
    console.log(fileId + " to be deleted");
    try {
      let authorization = getStorage(Constants.AUTHORIZATION);
      if (authorization === null || authorization === undefined) {
        //Todo abstract the same to a hook
        alert('The property is not found in local storage.');
      } else {
        const response = await fetch(URLConstants.BASE_URL + URLConstants.FILE_DELETE_ENDPOINT + `?fileId=${fileId}`, {
          method: 'GET',
          headers: {
            "Authorization": `${authorization}`,
            "Content-Type": "application/json"
          }
        });

        if (response.ok) {

          const data = await response.json()
          console.log(data)
          if(data.response.status===200){
            let newDownloadingArray = downloadingFiles.filter(id => id !== fileId);
            setDownloadingFiles(newDownloadingArray);
            let newFilesArray = files.filter(item => item.id !== fileId);
            setFiles(newFilesArray);
            toast.success("File deleted successfully");
          }else{
            toast.error("File could'nt be deleted")
          }
        } else {
          toast.error("Something went wrong")
        }
      }
    } catch (error) {
      console.log(error)
    } 
  }
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
          const data = await response.json()
          console.log(data)
          if(data.response.status===200){

            setIsOpen(false);
            console.log("Before File Add Handler " + downloadingFiles)
            let newDownloadingArray = [...downloadingFiles,data.data.id];
            setDownloadingFiles(newDownloadingArray);
            console.log("AfterFile Add Handler " + downloadingFiles)
            console.log(downloadingFiles);
            
            let newFilesArray = [data.data, ...files];
            setFiles(newFilesArray);
            
            toast.success("File added successfully");
          }else{
            toast.error(data.response.msg)
          }
        } else {
          toast.error("Something went wrong")
        }
      }
    } catch (error) {
      console.log(error)
    } 
  }

  const fetchFiles = useCallback(async () => {
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
          console.log(data);
          setFiles(data.data);
          console.log("Before Fetching Files " + downloadingFiles)
          const filteredData = data.data.filter(item => item.progress !== 100).map(item => item.id);
          //Todo check here what should be the case
          // let newDownloadingArray = [filteredData, ...downloadingFiles]
          setDownloadingFiles(filteredData);
          console.log("After Fetching Files " + downloadingFiles)
        }
      }
    } catch (error) {
      console.log(error)
    }
  }, [])

  let eventSource;
  const startStreaming = () => {
    if(downloadingFiles.length === 0){
      console.log("Nothing to stream for");
      return;
    }
    console.log("Starting Stream")
    setIsStreaming(true);
    console.log(eventSource);
    if (eventSource != null || eventSource != undefined) {

      eventSource.close();
    }
    let sseUrl = URLConstants.BASE_URL + URLConstants.FILES_STREAM_ENDPOINT + `?ids=${downloadingFiles.join(',')}`;
    eventSource = new EventSource(sseUrl);
    eventSource.onopen = (msg) => {
      console.log(msg);
    }
    eventSource.onmessage = (event) => {
      const parsedData = JSON.parse(event.data)
      console.log(parsedData);
      const mp = new Map()
      parsedData.files.forEach((file) => {
        mp.set(file.id, file.progress);
      })

      files.forEach(file => {
        if (mp.get(file.id) !== undefined) {
          file.progress = mp.get(file.id);
        }
      })
      setFiles([...files])
      console.log("Before 100% filtering " +downloadingFiles)
      let newdownloadingArray = downloadingFiles.filter((id) => (mp.get(id) !== undefined && mp.get(id) != 100));
      if(downloadingFiles.length !== newdownloadingArray.length){
        setDownloadingFiles(newdownloadingArray)
      }
      console.log("After 100% filtering " +downloadingFiles)
    }
    eventSource.onerror = (error) => {
      console.error('EventSource failed:', error);
      console.log("Streaming stopped " + isStreaming)
      setIsStreaming(false);
      eventSource.close();
    };
  }

  useEffect(()=>{
    console.log("useEffect: isStreaming Updated " + isStreaming)
    if(isStreaming === false && downloadingFiles.length > 0){
      console.log("Need to restart the streaming");  
      console.log(downloadingFiles);
      startStreaming();
    }
  },[isStreaming])

  useEffect(() => {
    console.log("useEffect: downloadingFiles updated " + downloadingFiles)
    startStreaming();
  }, [downloadingFiles])

  useEffect(() => {
    console.log("useEffect: Initial useEffect Triggered")
    fetchFiles();
  }, [fetchFiles]);

  return (
    <>
      <div className="action-container flex justify-end mb-4">
        <button onClick={() => setIsOpen(true)} className="block text-white bg-picton-blue hover:bg-picton-blue-800 focus:ring-4 focus:outline-none focus:ring-picton-blue font-medium rounded-lg text-sm px-5 py-2.5 text-center">Add File</button>
        <Dialog open={isOpen} onClose={() => setIsOpen(false)} className="relative z-50">
          <div className="fixed inset-0 flex flex-col w-screen items-center justify-center p-4 bg-raisin-black opacity-90">
            <div className="dialog-head flex items-center justify-between p-4 md:p-5 border-b rounded-t">
              <h3 className="text-lg font-semibold text-white">
                Download New File
              </h3>
              <button onClick={() => setIsOpen(false)} type="button" className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center" data-modal-toggle="crud-modal">
                <svg className="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                  <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                </svg>
                <span className="sr-only">Close modal</span>
              </button>
            </div>
            <form className="p-4 md:p-5" onSubmit={handleFileAdd}>
              <div className="grid gap-4 mb-4 grid-cols-2">
                <div className="col-span-2">
                  <label htmlFor="name" className="block mb-2 text-sm font-medium text-gray-900">File Name</label>
                  <input type="text" name="fileName" className="bg-alice-blue border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="Type file name" required="" />
                </div>
                <div className="col-span-2">
                  <label htmlFor="name" className="block mb-2 text-sm font-medium text-gray-900 ">File Url</label>
                  <input type="text" name="fileUrl" className="bg-alice-blue border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="Type file url" required="" />
                </div>
              </div>
              <button type="submit" className="text-white inline-flex items-center bg-picton-blue hover:bg-picton-blue-800 focus:ring-4 focus:outline-none focus:ring-picton-blue font-medium rounded-lg text-sm px-5 py-2.5 text-center">
                <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                Download
              </button>
            </form>
          </div>
        </Dialog>


      </div>

      {files && files.map((file, index) => (
        <File key={index} handleDelete={handleFileDelete} fileId = {file.id} fileName={file.fileName} downloadedSize={file.downloadedSize} progress={file.progress} fileSize={file.fileSize} />
      ))}
    </>
  )
}
