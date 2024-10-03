export interface GenericResponse<T> {
    data: T;
    response: ResponseStatus;
}

export interface ResponseStatus {
    status: number;
    msg: string;
    description: string;
}