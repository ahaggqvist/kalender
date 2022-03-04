export default interface Account {
  id: null,
  displayName: string,
  username: string,
  password: null,
  mail: string,
  firstName: string,
  lastName: string,
  authorities: [
    {
      authority: string
    }
  ],
  enabled: true,
  accountNonExpired: true,
  accountNonLocked: true,
  credentialsNonExpired: true
}
