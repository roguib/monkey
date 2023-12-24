export class FetchMock {
  constructor() {
    this.mockedData = [];

    // override global fetch
    window.fetch = (mockedUri) => {
      const mockedResponse = this.mockedData.find(({ uri }) => uri === mockedUri);
      if (!mockedResponse) {
        console.log('no uri found');
      }
      return {
        data: { ok: true },
        json: () => mockedResponse.response
      };
    };
  }

  addMock(uri, response) {
    this.mockedData.push({ uri, response });
  }
}