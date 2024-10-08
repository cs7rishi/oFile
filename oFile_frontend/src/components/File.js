import React from 'react'

export const File = ({ fileName, downloadedSize, progress, fileSize }) => {
  return (
    <>
      <div className="file-container flex items-center bg-white rounded-lg border border-gray-200 shadow-md p-4 mb-4">
        <div className="thumbnail w-max">
          <svg className="w-[48px] h-[48px] text-red-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
            <path stroke="currentColor" stroke-linejoin="round" stroke-width="2" d="M10 3v4a1 1 0 0 1-1 1H5m14-4v16a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V7.914a1 1 0 0 1 .293-.707l3.914-3.914A1 1 0 0 1 9.914 3H18a1 1 0 0 1 1 1Z" />
          </svg>

        </div>
        <div className="file-description w-full">
          <div className="file-header flex justify-between">
            <div className="file-name">
              {fileName}
            </div>
            <div className="file-action">
              <svg className="w-6 h-6 text-red-700" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18 17.94 6M18 18 6.06 6" />
              </svg>
            </div>
          </div>
          <div className="file-status-container w-full flex flex-col mt-2">
            <div className="progress-bar w-full bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
              <div className="bg-blue-600 h-2.5 rounded-full" style={{ width: `${progress}%` }}></div>
            </div>
            <div className="download-status w-full flex flex-row justify-between">
              <div className="file-size">
                {`${downloadedSize} ${fileSize}`}
              </div>
              <div className="download-speed">
                895MBps
              </div>
            </div>
          </div>

        </div>
      </div>
    </>
  )
}
