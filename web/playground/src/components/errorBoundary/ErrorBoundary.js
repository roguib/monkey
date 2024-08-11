// ErrorBoundary component can't be created as a function, but since
// we're using this component only to display a navigation error in
// react router it's fine as it is
function ErrorBoundary({ fallback }) {
  return fallback;
}

export default ErrorBoundary;