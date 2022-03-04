export default interface Appointment {
  id?: string,
  backgroundColor?: string,
  folderName?: string,
  folderId: string,
  extendedProps?: {
    createdBy?: string,
    location?: string,
    lastModifiedName?: string,
    dateTimeCreated?: string,
    organizerMail?: string,
    organizerName?: string,
    body?: string,
    attendees?: [
      {
        displayName: string,
        mail: string
      }
    ],
    icalUid?: string
  },
  start: string,
  end: string,
  title: string
}
