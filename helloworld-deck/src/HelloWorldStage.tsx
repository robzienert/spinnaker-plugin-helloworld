import React from 'react';
import {
  ExecutionDetailsSection, ExecutionDetailsTasks, FormikFormField, FormikStageConfig, HelpField,
  IExecutionDetailsSectionProps, IStageConfigProps, IStageTypeConfig, TextInput
} from '@spinnaker/core';
import './HelloWorldStage.less';

const HelloWorldConfig = (props: IStageConfigProps) => (
    <FormikStageConfig {...props} onChange={props.updateStage} render={() => (
        <FormikFormField name="recipient" label="Hello Recipient" required={true}
            help={<HelpField content="Say hello to my little friend!"/>}
            input={props => <TextInput placeholder="whirled" {...props} />}
        />
    )}/>
);

export function HelloWorldDetails(props: IExecutionDetailsSectionProps & { title: string }) {
  return (
      <ExecutionDetailsSection name={props.name} current={props.current}>
        <div className="helloworld v2"><p>{props.stage.outputs.message}</p></div>
      </ExecutionDetailsSection>
  )
}

export const helloWorldStage: IStageTypeConfig = {
  key: 'helloWorld',
  label: `Hello World`,
  description: 'A custom stage to demonstrate plugins',
  component: HelloWorldConfig, // stage config
  executionDetailsSections: [HelloWorldDetails, ExecutionDetailsTasks],
};

export namespace HelloWorldDetails {
  export const title = 'helloWorld';
}
